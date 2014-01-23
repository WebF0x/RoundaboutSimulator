package dev2.observer;

public interface Observable
{

	/**
	 * Adds an observer to SettingsList
	 * 
	 * @param obs
	 *            - the new observer
	 */
	public void addObserver(Observer obs);

	/**
	 * Sends new settings to observers
	 */
	public void updateObserver();

	/**
	 * Removes all observers from SettingsList
	 */
	public void removeObserver();

}// interface Observable
