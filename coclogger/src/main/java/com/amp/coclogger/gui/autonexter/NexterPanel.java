package com.amp.coclogger.gui.autonexter;

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
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.amp.coclogger.gui.util.AppControl;
import com.amp.coclogger.math.ResourceData;
import com.amp.coclogger.prefs.PrefName;

public class NexterPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(NexterPanel.class);
	
	private ExecutorService exec = null;
	
	private final Map<String, Parameter> paramMap = new HashMap<>();
	
	
	public NexterPanel(List<Parameter> parameters){
		for(Parameter parm : parameters){
			paramMap.put(parm.getName(), parm);
		}
		
		JButton btnBegin = new JButton("Begin");
		btnBegin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
				NexterHandler nh = new NexterHandler();
				exec.execute(nh);
			}
		});
		add(btnBegin);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(exec != null){
					exec.shutdownNow();
				}
				
			}
		});
		add(btnCancel);
	}
	
	private class NexterHandler implements Runnable {
		
		@Override
		public void run() {
			//zoom all the way out
			zoomOut();
			clickAttack();
			clickFindAMatch();
			//TODO click through shield notifier if necessary
			beginNexting();			
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
		
		private void clickNext(){
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
			while(true){
				int timeOut = ((IntegerParameter)paramMap.get(AutoNexter.PARAM_TIMEOUT)).getInt();
				ResourceCallable rc = new ResourceCallable(timeOut);
				Future<ResourceData> futureResource = exec.submit(rc);
				try {
					ResourceData resourceData = futureResource.get();
					if(resourceData == null){
						logger.warn("Timed out");
						return;
					} else if (meetsCriteria(resourceData)){
						logger.info("Sufficient Resources found! " + resourceData.toString());
						return;
					} else {
						logger.info("Insufficient Resources, continuing to search. " + resourceData.toString());
						clickNext();
						Thread.sleep(2000);
						continue;
					}
					
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
					logger.warn("Resource reading cancelled, bailing");
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
