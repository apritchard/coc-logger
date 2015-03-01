package com.amp.coclogger.prefs;

import java.util.List;

public interface PreferenceListener {
	void notify(List<PrefName> changedPrefs);
}
