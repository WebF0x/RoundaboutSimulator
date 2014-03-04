/*
    Copyright (c) 2013, Maxime Dupuis, Philippe Roy Villeneuve 
*/

package roundaboutSimulator.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

import roundaboutSimulator.observer.Observer;
import roundaboutSimulator.roundabout.Roundabout;


public class GraphPanel extends JPanel implements Observer
{

	private static final long	serialVersionUID	= -4215966169063948951L;

	JLabel						label;
	private boolean				m_isDisplaying		= true;
	private Roundabout				m_roundabout;
	private BufferedImage		m_noResults;
	private BufferedImage		m_title;
	private float				minVal				= 0;
	private int					m_red;
	private int					m_yellow;
	private int					m_green;

	public GraphPanel()
	{
		loadImages();
	}

	@Override
	public void paintComponent(Graphics g)
	{
		setBackground(g);
		if (!isInitialized())
		{
			noResults(g);
		}
		else
			if (isDisplaying())
			{
				// Display results
				display(g);
			}
		updateGraph();
	}

	@Override
	public void update(int[] args)
	{
		display(true);
		updateGraph();
	}// update Override

	/**
	 * Loads the images needed for the GraphPanel
	 */
	public void loadImages()
	{
		try
		{
			m_noResults = ImageIO.read(new File("image/no_results.png"));
			m_title = ImageIO.read(new File("image/title.png"));
		}
		catch (IOException ex)
		{
			System.out.println("Erreur: Les images n'ont pas pu ouvrir!");
		}
	}// loadImage

	public void display(boolean arg)
	{
		m_isDisplaying = arg;
	}

	/**
	 * Returns true if the GraphPanel received values other than zero. Returns
	 * false otherwise.
	 * 
	 * @return boolean
	 */
	public boolean isInitialized()
	{
		if (m_red == 0 && m_green == 0 && m_yellow == 0) return false;
		return true;
	}

	/**
	 * Returns true if the GraphPanel is displaying. Returns false otherwise.
	 * 
	 * @return boolean
	 */
	public boolean isDisplaying()
	{
		return m_isDisplaying;
	}

	/**
	 * Updates the GraphPanel with the newest values from Roundabout.
	 */
	private void updateGraph()
	{
		if (m_roundabout != null)
		{
			Vector<Integer> res = m_roundabout.getCirculationStatisticalData();
			setGreen(res.get(0));
			setYellow(res.get(1));
			setRed(res.get(2));
		}
	}

	/**
	 * Lays out the background for the GraphPanel.
	 * 
	 * @param g
	 *            - Graphics
	 */
	private void setBackground(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(new Color(238, 238, 238));
		g2d.fillRect(0, 0, getWidth(), getHeight());

		// Place Axis
		g2d.setColor(Color.BLACK);
		minVal = Math.min(4 * getWidth() / 5, getHeight());
		// X Axis
		g2d.fillRect((int) (minVal / 5f), (int) (9f * minVal / 10f), (int) (4f * minVal / 5f), (int) (minVal / 32f));

		// Y Axis
		g2d.fillRect((int) (minVal / 5f), (int) (9f * minVal / 10f), (int) (minVal / 40f), (int) (-6f * minVal / 10f));

		g2d.drawLine((int) (minVal / 5f), (int) (3f * minVal / 10f), (int) (5f * minVal / 5f), (int) (3f * minVal / 10f));
		g2d.drawLine((int) (minVal / 5f), (int) (5f * minVal / 10f), (int) (5f * minVal / 5f), (int) (5f * minVal / 10f));
		g2d.drawLine((int) (minVal / 5f), (int) (7f * minVal / 10f), (int) (5f * minVal / 5f), (int) (7f * minVal / 10f));

		g2d.drawImage(m_title, (int) (minVal / 10f), 0, (int) (minVal), (int) (3f * minVal / 10f), null);
	}

	/**
	 * Draws a String indicating that no results have been compiled so far.
	 * 
	 * @param g
	 *            - Graphics
	 */
	private void noResults(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);

		g2d.drawImage(m_noResults, (int) (3f * minVal / 10f), (int) (5f * minVal / 10f), (int) (3f * minVal / 5f), (int) (minVal / 5f), null);
	}

	/**
	 * Displays the results received from the Roundabout.
	 * 
	 * @param g
	 *            - Graphics
	 */
	private void display(Graphics g)
	{
		// Draw the graph
		Graphics2D g2d = (Graphics2D) g;
		float refValue = Math.max(getGreen(), Math.max(getYellow(), getRed()));
		float totValue = getGreen() + getRed() + getYellow();

		// Red Value
		g2d.setColor(Color.RED);
		g2d.fillRect((int) (7f * minVal / 20f), (int) (9f * minVal / 10f), (int) (minVal / 10f), (int) (-3f * minVal / 5f * getRed() / refValue));

		// Yellow Value
		g2d.setColor(Color.YELLOW);
		g2d.fillRect((int) (11f * minVal / 20f), (int) (9f * minVal / 10f), (int) (minVal / 10f), (int) (-3f * minVal / 5f * getYellow() / refValue));

		// Green Value
		g2d.setColor(Color.GREEN);
		g2d.fillRect((int) (15f * minVal / 20f), (int) (9f * minVal / 10f), (int) (minVal / 10f), (int) (-3f * minVal / 5f * getGreen() / refValue));

		g2d.setColor(Color.BLACK);

		g2d.drawString("" + (int) (100f * refValue / totValue), (minVal / 20f), (15f * minVal / 40f));

		g2d.drawString("" + (int) (66.66f * refValue / totValue), (minVal / 20f), (22f * minVal / 40f));

		g2d.drawString("" + (int) (33.33f * refValue / totValue), (minVal / 20f), (29f * minVal / 40f));

		g2d.drawString("0", minVal / 20f, 9f * minVal / 10f);

	}

	/**
	 * Defines a Roundabout from which GraphPanel must take its results.
	 * 
	 * @param roundabout
	 *            - Roundabout
	 */
	public void addRoundabout(Roundabout roundabout)
	{
		m_roundabout = roundabout;
	}

	// Getters
	public float getGreen()
	{
		return (float) m_green;
	}

	public float getYellow()
	{
		return (float) m_yellow;
	}

	public float getRed()
	{
		return (float) m_red;
	}

	// Setters
	private void setGreen(int green)
	{
		m_green = green;
	}

	private void setYellow(int yellow)
	{
		m_yellow = yellow;
	}

	private void setRed(int red)
	{
		m_red = red;
	}

}// class GraphPanel
