package dev2.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dev2.observer.Observer;
import dev2.rotary.Rotary;

public class ListPanel extends JPanel implements Observer
{

	private static final long	serialVersionUID	= 3880729606793504968L;

	private BoxLayout			list;
	private JLabel				stateLabel			= new JLabel("  State: ");
	private Rotary				m_rotary;
	private JLabel				avTime				= new JLabel("  Average time: ");
	private JLabel				allCars				= new JLabel("  Number of car generated: ");
	private JLabel				disCars				= new JLabel("  Number of car disposed: ");
	private JLabel				nbCars				= new JLabel("  Number of car simulated: ");
	private JLabel				carsRot				= new JLabel("  Number of car in the rotary: ");
	private JLabel				medTime				= new JLabel("  Medium Time: ");
	private JLabel				badTime				= new JLabel("  Bad Time: ");
	private JLabel				simTime				= new JLabel("  Simulation Time: ");

	private int					averageTime			= 0;

	/**
	 * Default constructor
	 */
	public ListPanel()
	{
		list = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(list);

		// State indicator
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(stateLabel);

		add(panel);
		add(avTime);
		add(allCars);
		add(disCars);
		add(nbCars);
		add(carsRot);
		add(medTime);
		add(badTime);
		add(simTime);

		panel.setAlignmentX(LEFT_ALIGNMENT);

	}// Constructor (default)

	@Override
	public void update(int[] args)
	{
		refreshLabels();
	}// update Override

	@Override
	public void paintComponent(Graphics g)
	{
		g.clearRect(0, 0, getWidth(), getHeight());	// Clears the panel
		g.setColor(new Color(238, 238, 238));
		g.fillRect(0, 0, getWidth(), getHeight());

		if (m_rotary != null)	// m_rotary exists
		{
			averageTime = m_rotary.getAverageVehicleLifeTime();

			// Pick the right color
			Color color;
			if (averageTime <= m_rotary.getMediumTime())
			{
				color = Color.GREEN;
			}
			else
				if (averageTime <= m_rotary.getBadTime())
				{
					color = Color.YELLOW;
				}
				else
					color = Color.RED;

			// Calculate where to draw the indicator
			int x = stateLabel.getX() + 50;
			int y = stateLabel.getY() + 1;
			int width = stateLabel.getHeight() - 1;
			int height = width;

			// Indicator outline
			g.setColor(Color.BLACK);
			g.fillRect(x, y, width, height);

			// Indicator color
			g.setColor(color);
			g.fillRect(x + 1, y + 1, width - 2, height - 2);
		}

		refreshLabels();
	}

	/**
	 * Associate this ListPanel with a single Rotary ListPanel will use this
	 * Rotary to calculate the statistic data
	 * 
	 * @param rotary
	 */
	public void setRotary(Rotary rotary)
	{
		m_rotary = rotary;
	}

	/**
	 * Updates the information displayed by the labels
	 */
	private void refreshLabels()
	{
		avTime.setText("  Average time: " + formatTime(averageTime));
		allCars.setText("  Number of car generated: " + (numberOfCars() + m_rotary.vehicleCount()));
		disCars.setText("  Number of car disposed: " + numberOfCars());
		nbCars.setText("  Number of car simulated: " + m_rotary.vehicleCount());
		carsRot.setText("  Number of car in the rotary: " + m_rotary.vehicleCountRot());
		medTime.setText("  Medium Time: " + formatTime(m_rotary.getMediumTime()));
		badTime.setText("  Bad Time: " + formatTime(m_rotary.getBadTime()));
		simTime.setText("  Simulation Time: " + formatElapseTime(m_rotary.getSimulationTime()));
	}

	/**
	 * Returns the number of cars taken into account for the statistics These
	 * cars have all entered and left the rotary
	 * 
	 * @return
	 */
	private int numberOfCars()
	{
		Vector<Integer> res = m_rotary.getCirculationStatisticalData();
		return res.get(0) + res.get(1) + res.get(2);
	}

	/**
	 * Format the time from milliseconds to human readable format
	 * 
	 * @param milliseconds
	 *            Time to format
	 * @return String containing the human readable time
	 */
	private String formatElapseTime(int milliseconds)
	{
		int tenth, seconds, minutes, hours;

		hours = milliseconds / (1000 * 60 * 60);
		milliseconds %= (1000 * 60 * 60);

		minutes = milliseconds / (1000 * 60);
		milliseconds %= (1000 * 60);

		seconds = milliseconds / 1000;
		milliseconds %= 1000;

		tenth = milliseconds / 100;

		return clockFormat(hours) + ":" + clockFormat(minutes) + ":" + clockFormat(seconds) + "." + tenth;
	}

	/**
	 * Formats a int to form a digital clock. Adds zeros when necessary.
	 * 
	 * @param value
	 *            Int to format
	 * @return Formatted string
	 */
	private String clockFormat(int value)
	{
		if (value / 10 == 0)
		{
			if (value / 1 == 0)
			{
				return "00";
			}
			return "0" + value;
		}
		return "" + value;
	}

	/**
	 * Converts an amount of seconds to a human readable format
	 * 
	 * @param seconds
	 * @return Formatted String
	 */
	private String formatTime(int seconds)
	{
		String mess = new String();

		int minutes, hours, days;

		days = seconds / (60 * 60 * 24);
		seconds = seconds % (60 * 60 * 24);

		hours = seconds / (60 * 60);
		seconds = seconds % (60 * 60);

		minutes = seconds / 60;
		seconds = seconds % 60;

		// Ex: "2 days, 3 minutes, 4 seconds"
		mess += format("day", days) + format("hour", hours) + format("minute", minutes) + format("second", seconds);

		return mess;

	}

	/**
	 * Modify a word to take into account the quantity
	 * 
	 * @param word
	 *            Word to modify
	 * @param quantity
	 * @return Depending of the quantity: the word, the word with a "s" or
	 *         nothing
	 */
	private static String format(String word, int quantity)
	{
		String mess = new String();

		if (quantity > 0)
		{
			mess += String.valueOf(quantity) + " " + word;	// Ex: "2 day"

			if (quantity > 1) mess += "s";					// Ex: "2 days"

			mess += " ";									// Ex: "2 days "
		}
		else
			if (word.equals("second"))
			{
				mess = String.valueOf(quantity) + " " + word;
			}

		return mess;
	}

}// class ListPanel
