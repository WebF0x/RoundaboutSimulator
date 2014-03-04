/*
    Copyright (c) 2013, Maxime Dupuis, Philippe Roy Villeneuve 
*/

package roundaboutSimulator.ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import roundaboutSimulator.roundabout.Roundabout;


public class RenderLayout extends GridLayout
{

	private static final long	serialVersionUID	= 5687202636493409517L;

	RenderWindow				m_window;
	private Roundabout				m_roundabout			= new Roundabout();
	private SettingsList		m_settings			= new SettingsList();
	private GraphPanel			m_graph				= new GraphPanel();
	private ListPanel			m_list				= new ListPanel();
	private JButton				m_startPause		= new JButton("Start");
	private JButton				m_speed				= new JButton("Speed X10");
	private boolean				m_isPaused			= true;

	/**
	 * This constructor takes the containing RenderWindow as a reference.
	 * 
	 * @param window
	 *            - RenderWindow
	 */
	public RenderLayout(RenderWindow window)
	{
		super(2, 2);// GridLayout constructor

		this.m_window = window;

		window.getContentPane().add(m_roundabout);
		window.getContentPane().add(m_settings);
		window.getContentPane().add(m_list);
		window.getContentPane().add(m_graph);

		m_settings.addObserver(m_graph);
		m_settings.addObserver(m_list);
		m_settings.addObserver(m_roundabout);

		m_graph.addRoundabout(m_roundabout);
		m_list.setRoundabout(m_roundabout);

		m_settings.addButton(m_startPause);
		m_settings.addButton(m_speed);

		m_startPause.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				pause(!m_startPause.getText().equals("Start"));
			}
		});

		m_speed.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				superSpeed(m_speed.getText().equals("Speed X10"));
			}
		});

	}// Constructor

	/**
	 * Refreshes the roundabout if it isn't paused. Calls the method repaint of
	 * Roundabout.
	 */
	public void refresh()
	{
		m_list.repaint();
		m_roundabout.repaint();
		m_graph.repaint();
	}// refresh

	/**
	 * Calculates the position the roundabout if it isn't paused. Calls the method
	 * repaint of Roundabout.
	 */
	public void calculate()
	{
		if (m_window.getLoopCount() % (int) ((float) RenderWindow.FPS * 60f / (float) m_roundabout.getCarFlow()) == 0)
		{
			m_roundabout.generateVehicle();
		}
		m_roundabout.calculate();
	}// refresh

	/**
	 * This method pauses the roundabout if the boolean sent is true. It resumes it
	 * if it's false.
	 * 
	 * @param arg
	 *            - boolean
	 */
	public void pause(boolean arg)
	{
		m_isPaused = arg;
		m_roundabout.updateTimes();
		if (m_isPaused)
		{
			m_startPause.setText("Start");
			m_window.stop();
		}
		else
		{
			m_startPause.setText("Pause");
			m_window.start();
		}
	}// pause

	/**
	 * Activate/Deactivate Super Speed
	 * 
	 * @param arg
	 *            true to activate, false to deactivate
	 */
	public void superSpeed(boolean arg)
	{
		m_window.setSuperSpeed(arg);
		if (arg)
		{
			m_roundabout.setTimeFactor(10);
			m_speed.setText("Normal Speed");
		}
		else
		{
			m_roundabout.setTimeFactor(1);
			m_speed.setText("Speed X10");
		}
	}// superSpeed

	public boolean isPaused()
	{
		return m_isPaused;
	}

}// class RenderLayout
