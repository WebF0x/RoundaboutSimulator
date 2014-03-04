/*
    Copyright (c) 2013, Maxime Dupuis, Philippe Roy Villeneuve 
*/

package roundaboutSimulator.roundabout;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class TrafficLight
{
	public static final float	WIDTH	= 1.63124342341f;	// Metres

	private float				m_x;						// Metres
	private float				m_y;						// Metres

	private Roundabout				m_roundabout;
	private Color				m_color	= new Color(0);	// Green / Red

	private Timer				m_timer;
	private int					m_greenTime, m_redTime;	// Seconds

	/**
	 * Constructor.
	 * 
	 * @param roundabout
	 *            Places the TrafficLight on this Roundabout
	 * @param greenTime
	 *            Time length of the green light (seconds)
	 * @param redTime
	 *            Time length of the red light (seconds)
	 */
	public TrafficLight(Roundabout roundabout, int greenTime, int redTime)
	{
		m_roundabout = roundabout;
		m_color = Color.GREEN;

		m_greenTime = greenTime;
		m_redTime = redTime;

		m_timer = new Timer(delayNeeded(), new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				toggle();
			}
		});
	}// Constructor

	/**
	 * Constructor
	 * 
	 * @param roundabout
	 *            Places the TrafficLight on this Roundabout
	 * @param halfPeriod
	 *            Toggles between red light and green light at this time
	 *            interval in seconds
	 */
	public TrafficLight(Roundabout roundabout, int halfPeriod)
	{
		this(roundabout, halfPeriod, halfPeriod);
	}// Constructor

	/**
	 * Calculate the delay needed until the next toggle.
	 * 
	 * @return int Delay needed (ms)
	 */
	private int delayNeeded()
	{
		int inSimTime = 1;

		if (m_color.equals(Color.RED))
			inSimTime = m_redTime;
		else
			if (m_color.equals(Color.GREEN)) inSimTime = m_greenTime;

		int delay = inSimTime * 1000 / m_roundabout.getTimeFactor();
		return delay;
	}

	/**
	 * Paint itself on a Graphics Object
	 * 
	 * @param g
	 *            Graphics Object
	 */
	public void paintTrafficLight(Graphics g)
	{
		int width = m_roundabout.meterToPixel(WIDTH);
		int height = m_roundabout.getLaneWidthInPixel() / 2;
		int x = m_roundabout.meterToPixel(m_x) - width / 2;
		int y = m_roundabout.meterToPixel(m_y) - height;

		// Contour
		g.setColor(Color.BLACK);
		g.fillRect(x, y, width, height);

		// Deux lumières
		g.setColor(m_color);
		int widthLum = width / 2;
		int heightLum = height / 6;
		int xLum = x + widthLum / 2;
		int yLum = y + height / 6;
		g.fillRect(xLum, yLum, widthLum, heightLum);

		yLum += height / 2;
		g.fillRect(xLum, yLum, widthLum, heightLum);

	}// paintTrafficLight

	// Getters
	public void setX(float x)
	{
		m_x = x;
	}

	public float getX()
	{
		return m_x;
	}

	public void setY(float y)
	{
		m_y = y;
	}

	public float getY()
	{
		return m_y;
	}

	/**
	 * Sets the Color of the TrafficLight
	 * 
	 * @param c
	 *            Color.RED or Color.GREEN
	 * @throws IllegalArgumentException
	 *             if color isn't red or green
	 */
	public void setColor(Color c) throws IllegalArgumentException
	{
		if (c.equals(Color.GREEN) || c.equals(Color.RED))
		{
			m_color = c;
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Set the toggle interval
	 * 
	 * @param halfPeriod
	 *            Interval at which to toggle between red light and green light
	 *            (seconds)
	 */
	public void setHalfPeriod(int halfPeriod)
	{
		setGreenTime(halfPeriod);
		setRedTime(halfPeriod);

		restart();
	}

	/**
	 * Toggle color from red to green or vice-versa.
	 */
	public void toggle()
	{
		if (m_color.equals(Color.GREEN))
			setColor(Color.RED);
		else
			if (m_color.equals(Color.RED)) setColor(Color.GREEN);

		start();
	}

	/**
	 * Use to deactivate the color toggling.
	 */
	public void freezeOnGreen()
	{
		m_timer.stop();
		m_color = Color.GREEN;
	}

	/**
	 * Set the timer and start color toggling.
	 */
	public void start()
	{
		int delay = delayNeeded();

		if (delay > 0)
		{
			m_timer.setDelay(delay);
			m_timer.start();
		}
		else
		{
			freezeOnGreen();
		}
	}

	/**
	 * Stop color toggling.
	 */
	public void stop()
	{
		m_timer.stop();
	}

	public Color getColor()
	{
		return m_color;
	}

	public int getGreenTime()
	{
		return m_greenTime;
	}

	/**
	 * Also restarts the timer
	 * 
	 * @param greenTime
	 */
	public void setGreenTime(int greenTime)
	{
		m_greenTime = greenTime;
		restart();
	}

	public int getRedTime()
	{
		return m_redTime;
	}

	/**
	 * Also restarts the timer
	 * 
	 * @param redTime
	 */
	public void setRedTime(int redTime)
	{
		m_redTime = redTime;
		restart();
	}

	/**
	 * Restart the timer.
	 */
	public void restart()
	{
		m_timer.setDelay(delayNeeded());
		stop();
		start();
	}

}// class TrafficLight

