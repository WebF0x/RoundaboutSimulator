package dev2.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.Timer;

public class RenderWindow extends JFrame
{

	private static final long	serialVersionUID	= -203061943978917754L;

	private RenderLayout		grid;
	private Timer				timer;
	public static final int		FPS					= 20;
	private int					timeFactor			= 1;
	private int					loopCount			= 0;

	/**
	 * Default constructor
	 */
	public RenderWindow()
	{
		// Window properties
		setTitle("Rotary Simulation");
		setMinimumSize(new Dimension(400, 400));
		setSize(new Dimension(600, 600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		grid = new RenderLayout(this);
		this.setLayout(grid);

		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				if (e.getID() == ComponentEvent.COMPONENT_RESIZED)
				{
					grid.pause(true);
				}
			}
		});

		timer = new Timer(1000 / timeFactor / FPS, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (getLoopCount() % timeFactor == 0)
				{
					grid.refresh();
				}
				grid.calculate();
				loopCount++;
			}
		});

		setVisible(true);

	}// Constructor (default)

	/**
	 * Starts the timer
	 */
	public void start()
	{
		timer.start();
	}

	/**
	 * Stops the timer
	 */
	public void stop()
	{
		timer.stop();
	}

	/**
	 * Activate/Deactivate Super Speed
	 * 
	 * @param true to activate SuperSpeed, false to deactivate
	 */
	public void setSuperSpeed(boolean arg)
	{
		if (arg)
			timeFactor = 10;
		else
			timeFactor = 1;
		timer.setDelay(1000 / timeFactor / FPS);
	}

	public int getLoopCount()
	{
		return loopCount;
	}

}// class RenderWindow
