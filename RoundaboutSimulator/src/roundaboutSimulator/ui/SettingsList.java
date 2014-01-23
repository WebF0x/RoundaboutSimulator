package roundaboutSimulator.ui;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import roundaboutSimulator.observer.*;


public class SettingsList extends JPanel implements Observable
{

	private static final long	serialVersionUID	= 2339123174419232861L;

	private static final int	REFRESH_DELAY		= 100;

	private static final int	MIN_LANES			= 2;
	private static final int	DEFAULT_LANES		= 4;
	private static final int	MAX_LANES			= 24;

	private static final int	MIN_RADIUS			= 10;
	private static final int	DEFAULT_RADIUS		= 15;
	private static final int	MAX_RADIUS			= 100;

	private static final int	MIN_STOP_TIME		= 0;
	private static final int	DEFAULT_STOP_TIME	= 10;
	private static final int	MAX_STOP_TIME		= 200;

	private static final int	MIN_CAR_FLOW		= 1;
	private static final int	DEFAULT_CAR_FLOW	= 20;
	private static final int	MAX_CAR_FLOW		= 200;

	private BoxLayout			list;
	private ArrayList<Observer>	observerList		= new ArrayList<Observer>();
	private Thread				observationThread;
	private JPanel				pan1				= new JPanel();
	private JPanel				pan2				= new JPanel();
	private JPanel				pan3				= new JPanel();
	private JPanel				pan4				= new JPanel();
	private JLabel				label1				= new JLabel("Number of lanes ");
	private JLabel				label2				= new JLabel("Radius (meter) ");
	private JLabel				label3				= new JLabel("Stop time (sec) ");
	private JLabel				label4				= new JLabel("Car per minute ");

	private JSpinner			lanes				= new JSpinner();
	private JSpinner			radius				= new JSpinner();
	private JSpinner			stopTime			= new JSpinner();
	private JSpinner			carFlow				= new JSpinner();

	private int					m_lanes;
	private int					m_radius;
	private int					m_stopTime;
	private int					m_carFlow;

	/**
	 * Default constructor
	 */
	public SettingsList()
	{

		this.init();

		observationThread = new Thread()
		{
			@Override
			public void run()
			{
				while (true)
				{
					try
					{
						updateObserver();
						Thread.sleep(REFRESH_DELAY);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		};

		observationThread.start();
	}// Constructor (default)

	/**
	 * Add a JButton to SettingsList
	 * 
	 * @param button
	 *            JButton to add
	 */
	public void addButton(JButton button)
	{
		JPanel pan = new JPanel();
		pan.add(button);
		add(pan);
		pan.setAlignmentX(RIGHT_ALIGNMENT);
	}

	/**
	 * Returns the number of lane.
	 * 
	 * @return int - Lanes
	 */
	public int getLanes()
	{
		m_lanes = Integer.parseInt(this.lanes.getValue().toString());
		if (m_lanes > MAX_LANES)
			return MAX_LANES;
		else
			if (m_lanes < MIN_LANES)
				return MIN_LANES;
			else
				return m_lanes;
	}// getLanes

	/**
	 * Returns the radius (meters).
	 * 
	 * @return int - Radius
	 */
	public int getRadius()
	{
		m_radius = Integer.parseInt(this.radius.getValue().toString());
		if (m_radius > MAX_RADIUS)
			return MAX_RADIUS;
		else
			if (m_radius < MIN_RADIUS)
				return MIN_RADIUS;
			else
				return m_radius;
	}// getRadius

	/**
	 * Returns the stop time (seconds).
	 * 
	 * @return int - Stop time
	 */
	public int getStopTime()
	{
		m_stopTime = Integer.parseInt(stopTime.getValue().toString());

		if (m_stopTime < MIN_STOP_TIME)
		{

			return MIN_STOP_TIME;
		}
		else
			if (m_stopTime > MAX_STOP_TIME) return MAX_STOP_TIME;

		return m_stopTime;
	}

	/**
	 * Returns the car flow (cars per minute)
	 * 
	 * @return int - Car flow
	 */
	public int getCarFlow()
	{
		m_carFlow = Integer.parseInt(carFlow.getValue().toString());

		if (m_carFlow < MIN_CAR_FLOW)
			return MIN_CAR_FLOW;
		else
			if (m_carFlow > MAX_CAR_FLOW) return MAX_CAR_FLOW;

		return m_carFlow;
	}

	/**
	 * Initializes the content of SettingsList. It is meant to be called from
	 * the constructor.
	 */
	protected void init()
	{
		pan1.setLayout(new BoxLayout(pan1, BoxLayout.LINE_AXIS));
		pan1.add(label1);
		lanes.setMinimumSize(new Dimension(50, 25));
		lanes.setMaximumSize(new Dimension(90, 25));
		pan1.add(lanes);

		pan2.setLayout(new BoxLayout(pan2, BoxLayout.LINE_AXIS));
		pan2.add(label2);
		radius.setMinimumSize(new Dimension(50, 25));
		radius.setMaximumSize(new Dimension(90, 25));
		pan2.add(radius);

		pan3.setLayout(new BoxLayout(pan3, BoxLayout.LINE_AXIS));
		pan3.add(label3);
		stopTime.setMinimumSize(new Dimension(50, 25));
		stopTime.setMaximumSize(new Dimension(90, 25));
		pan3.add(stopTime);

		pan4.setLayout(new BoxLayout(pan4, BoxLayout.LINE_AXIS));
		pan4.add(label4);
		carFlow.setMinimumSize(new Dimension(50, 25));
		carFlow.setMaximumSize(new Dimension(90, 25));
		pan4.add(carFlow);

		list = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		setLayout(list);

		pan1.setAlignmentX(RIGHT_ALIGNMENT);
		pan2.setAlignmentX(RIGHT_ALIGNMENT);
		pan3.setAlignmentX(RIGHT_ALIGNMENT);
		pan4.setAlignmentX(RIGHT_ALIGNMENT);

		add(pan1);
		add(pan2);
		add(pan3);
		add(pan4);

		lanes.setValue(DEFAULT_LANES);
		radius.setValue(DEFAULT_RADIUS);
		stopTime.setValue(DEFAULT_STOP_TIME);
		carFlow.setValue(DEFAULT_CAR_FLOW);

	}// init

	@Override
	public void addObserver(Observer obs)
	{
		this.observerList.add(obs);
	}// addObserver

	@Override
	public void removeObserver()
	{
		this.observerList = new ArrayList<Observer>();
	}// removeObserver

	@Override
	public void updateObserver()
	{
		if (settingsModified())
		{
			for (Observer obs : this.observerList)
				obs.update(getSettings());
		}
	}// updateObserver

	/**
	 * This method returns an integer array containing all the settings of
	 * SettingsList. It is meant to be called from within the overridden method
	 * updateObserver().
	 * 
	 * @return an integer array of the settings
	 */
	protected int[] getSettings()
	{
		int[] settings = new int[4];
		settings[0] = getLanes();
		settings[1] = getRadius();
		settings[2] = getStopTime();
		settings[3] = getCarFlow();
		return settings;
	}// getSettings

	/**
	 * Informs whether or not the settings have been modified
	 * 
	 * @return true if settings have been modified
	 */
	protected boolean settingsModified()
	{
		if (Integer.parseInt(lanes.getValue().toString()) != m_lanes || Integer.parseInt(radius.getValue().toString()) != m_radius
				|| Integer.parseInt(stopTime.getValue().toString()) != m_stopTime || Integer.parseInt(carFlow.getValue().toString()) != m_carFlow)
		{
			return true;
		}
		return false;
	}

}// class SettingsList
