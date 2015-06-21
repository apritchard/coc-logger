package com.amp.coclogger.gui.autonexter;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;

import com.amp.coclogger.gui.util.AppControl;
import com.amp.coclogger.gui.util.AudioUtil;
import com.amp.coclogger.math.DataUtils;
import com.amp.coclogger.math.ResourceData;
import com.amp.coclogger.prefs.PrefName;

public class NexterPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(NexterPanel.class);
	
	private ExecutorService exec = null;
	
	private final Map<String, Parameter> paramMap = new HashMap<>();
	private final JLabel lblAverageGold = new JLabel("0");
	private final JLabel lblAverageElixir = new JLabel("0");
	private final JLabel lblAverageDarkElixir = new JLabel("0");
	private final JLabel lblAverageTrophies = new JLabel("0");
	private final JLabel lblNexts = new JLabel("0");
	
	private final JButton btnAction;
	private final ActionListener actStart;
	private final ActionListener actCancel;
	private final ActionListener actResume;
	
	private long totalElixir;
	private long totalGold;
	private long totalDarkElixir;
	private long totalTrophies;
	private long totalNexts;
	
	private final String CANCEL_TEXT = "Cancel";
	private final String RESUME_TEXT = "Resume";
	private final String START_TEXT = "Start";
	
	
	public NexterPanel(List<Parameter> parameters){
		setLayout(new MigLayout());
		for(Parameter parm : parameters){
			paramMap.put(parm.getName(), parm);
		}
		
		actStart = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setActionButton(CANCEL_TEXT, actCancel);
				exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
				NexterHandler nh = new NexterHandler(NexterBehavior.START);
				exec.execute(nh);		
			}
		};
		
		actCancel = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setActionButton(START_TEXT, actStart);
				if(exec != null){
					exec.shutdownNow();
				}
			}
		};
		
		actResume = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setActionButton(CANCEL_TEXT, actCancel);
				exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
				NexterHandler nh = new NexterHandler(NexterBehavior.RESUME);
				exec.execute(nh);
			}
		};
		
		btnAction = new JButton(START_TEXT);
		btnAction.addActionListener(actStart);
		add(btnAction, "wrap");
		add(new JLabel("Nexts:"));
		add(lblNexts, "wrap");
		add(new JLabel("Avg. Gold:"));
		add(lblAverageGold, "wrap");
		add(new JLabel("Avg. Elix:"));
		add(lblAverageElixir, "wrap");
		add(new JLabel("Avg. D.E.:"));
		add(lblAverageDarkElixir, "wrap");

	}
	
	private void setActionButton(final String text, final ActionListener newListener){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				for(ActionListener l : btnAction.getActionListeners()){
					btnAction.removeActionListener(l);
				}
				btnAction.setText(text);
				btnAction.addActionListener(newListener);
			}
		});
	}
	private void updatePanelData(ResourceData rd){
		totalElixir += rd.getElixir();
		totalGold += rd.getGold();
		totalDarkElixir += rd.getDarkElixir();
		totalTrophies += rd.getTrophiesWon();
		totalNexts++;
		
		lblAverageElixir.setText(Long.toString(totalElixir/totalNexts));
		lblAverageGold.setText(Long.toString(totalGold/totalNexts));
		lblAverageDarkElixir.setText(Long.toString(totalDarkElixir/totalNexts));
		lblNexts.setText(Long.toString(totalNexts));
	}
	
	private enum NexterBehavior{
		START, RESUME;
	}
	
	private class NexterHandler implements Runnable {
		NexterBehavior nb;
		
		public NexterHandler(NexterBehavior nb){
			this.nb = nb;
		}
		
		@Override
		public void run() {
			switch(nb){
			case START:
				zoomOut();
				clickAttack();
				clickFindAMatch();
			case RESUME:
				beginNexting();
			default:
				break;
			}
		}
		
		private void zoomOut(){
			logger.info("Zooming out");
			int x = PrefName.COC_X.getInt();
			int y = PrefName.COC_Y.getInt();
			AppControl.zoomOutFull(x, y);
		}
		
		private void clickAttack(){
			logger.info("Clicking Attack");
			int x = PrefName.ATTACK_X.getInt();
			int y = PrefName.ATTACK_Y.getInt();
			AppControl.clickMouse(x, y);
		}
		
		private void clickFindAMatch(){
			logger.info("Finding a match");
			int x = PrefName.FIND_X.getInt();
			int y = PrefName.FIND_Y.getInt();
			AppControl.clickMouse(x, y);
		}
		
		private void clickNext() {
			logger.info("Clicking Next");
			int x = PrefName.NEXT_X.getInt();
			int y = PrefName.NEXT_Y.getInt();
			AppControl.clickMouse(x, y);
		}
		
		/**
		 * This should be called while zoomed all the way out and on or
		 * waiting for loading of battle screen
		 */
		private void beginNexting(){
			boolean first = true;
			while(true){
				int timeOut = ((IntegerParameter)paramMap.get(AutoNexter.PARAM_TIMEOUT)).getInt();
				ResourceCallable rc = new ResourceCallable(timeOut);
				Future<ResourceData> futureResource = exec.submit(rc);
				try {
					ResourceData resourceData = futureResource.get();
					if(DataUtils.isValid(resourceData)){
						updatePanelData(resourceData);
					}
					if(resourceData == null){
						logger.warn("Timed out");
						setActionButton(START_TEXT, actStart);
						AudioUtil.FAIL.play();
						return;
					} else if (DataUtils.isValid(resourceData) && meetsCriteria(resourceData)){
						logger.info("Sufficient Resources found! " + resourceData.toString());
						setActionButton(RESUME_TEXT, actResume);
						AudioUtil.DONE.play();
						return;
					} else {
						if(!DataUtils.isValid(resourceData)){
							logger.info("Invalid Resource line: \n" + resourceData.toString());
							AudioUtil.FAIL.play();
						} else {
							logger.info("Insufficient Resources, continuing to search. " + resourceData.toString());
						}
						if(!first && !MouseInfo.getPointerInfo().getLocation().equals(new Point(PrefName.NEXT_X.getInt(), PrefName.NEXT_Y.getInt()))){
							logger.warn("Mouse moved, bailing");
							setActionButton(RESUME_TEXT, actResume);
							return;
						}
						first = false;
						clickNext();
						Thread.sleep(2000);
						continue;
					}
					
				} catch (InterruptedException | ExecutionException e) {
					logger.warn("Resource reading cancelled, bailing");
					setActionButton(START_TEXT, actStart);
					return;
				}
			}
		}
		
		/**
		 * Returns true if this resourceData matches the requirements
		 * in the parameters
		 * @param rd
		 * @return
		 */
		private boolean meetsCriteria(ResourceData rd){
			int minGold = ((IntegerParameter)paramMap.get(AutoNexter.PARAM_GOLD)).getInt();
			int minElixir = ((IntegerParameter)paramMap.get(AutoNexter.PARAM_ELIXIR)).getInt();
			int minDarkElixir = ((IntegerParameter)paramMap.get(AutoNexter.PARAM_DARK_ELIXIR)).getInt();
			int minTrophiesWin = ((IntegerParameter)paramMap.get(AutoNexter.PARAM_TROPHIES_WIN)).getInt();
			logger.debug("Checking Resources: " + rd.toString());
//			int minTrophiesLost = ((IntegerParameter)paramMap.get(AutoNexter.PARAM_TROPHIES_LOST)).getInt();
			
			return 
					rd.getGold() > minGold &&
					rd.getElixir() > minElixir &&
					rd.getDarkElixir() > minDarkElixir &&
					rd.getTrophiesWon() > minTrophiesWin; // &&
//					rd.getTrophiesLost() > minTrophiesLost;
		}
		
	}

	
	// 0. figure out what screen we're on (skipping for now, assuming own town hall)
	// 1. zoom all the way out
	// 2. click "attack"
	// 3. click "find a match"
	// 3a. click Okay (skipping for now)
	//loop
	// 4. wait for clouds to clear
	// 5. do image parse jobs
	// 	a. Read resource/trophy numbers
	//  b. Identify enemy league
	//  c. Identify enemy townhall level
	// 6. if parameters met, stop, otherwise, click next
	//end loop

}
