package roundaboutSimulator.roundabout;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JPanel;

import roundaboutSimulator.observer.Observer;
import roundaboutSimulator.ui.RenderWindow;


public class Roundabout extends JPanel implements Observer
{
	private static final long	serialVersionUID	= 3760556495169242627L;

	private int					m_mediumTime		= 0;
	private int					m_badTime			= 0;

	private Vector<Long>		m_goodTimes			= new Vector<Long>();
	private Vector<Long>		m_mediumTimes		= new Vector<Long>();
	private Vector<Long>		m_badTimes			= new Vector<Long>();

	private int					m_timeFactor		= 1;
	private int					m_simulationTime	= 0;						// Milliseconds

	private int					m_nbLane;
	private int					m_radius;										// Meters
	private int					m_halfPeriod		= 1;						// Seconds

	// Cars per minute
	private int					m_carFlow			= 1;

	private int					m_radiusRoundaboutPixels;
	private int					m_radiusCenterPixels;
	private int					m_laneWidthInPixel;

	private TrafficLight		m_light;

	private ArrayList<Vehicle>	m_vehicles;

	private final Color			GRASS_COLOR			= new Color(58, 157, 35);

	/**
	 * Creates a standard roundabout.
	 */
	public Roundabout()
	{
		super();
		m_vehicles = new ArrayList<Vehicle>();
		Vehicle.loadImage();
		m_light = new TrafficLight(this, m_halfPeriod);

		updateTimes();
	}// Constructor (default)

	/**
	 * Converts meters to pixels. Use this method to keep proportions while
	 * drawing
	 * 
	 * @param float - meters
	 * @return int - Pixels
	 */
	public int meterToPixel(float meters)
	{
		// The radius of the roundabout takes up a fraction of the screen.
		final float fraction = 1 / 4f;

		final float ratio = fraction * Math.min(getWidth(), getHeight()) / (float) getRadius();

		int pixels = (int) (meters * ratio);

		return pixels;
	}// meterToPixel(float)

	/**
	 * Converts meters to pixels. Use this method to keep proportions while
	 * drawing
	 * 
	 * @param int - meters
	 * @return int - Pixels
	 */
	public int meterToPixel(int meters)
	{
		return meterToPixel((float) meters);
	}// meterToPixel(int)

	/**
	 * Converts pixels to meters. Use this method to keep proportions while
	 * drawing
	 * 
	 * @param int - pixels
	 * @return float - Meters
	 */
	public float pixelToMeter(int pixels)
	{
		// The radius of the roundabout takes up a fraction of the screen
		float fraction = 1 / 4f;

		float ratio = fraction * Math.min(getWidth(), getHeight()) / (float) getRadius();

		float meters = (float) pixels / ratio;

		return meters;
	}// pixelToMeter

	@Override
	public void update(int[] args)
	{
		if (getNbLane() != args[0] || getRadius() != args[1] || getStopTime() != args[2])
		{
			setNbLane(args[0]);
			setRadius(args[1]);
			setStopTime(args[2]);

			m_radiusRoundaboutPixels = meterToPixel(getRadius());
			m_radiusCenterPixels = meterToPixel(getRadius() - 2 * Vehicle.WIDTH);

			resetRoundabout();
			repaint();

			m_light.setHalfPeriod(m_halfPeriod);
			m_light.setX((this.m_radius * 1.5f));	// Place the traffic light
		}
		setCarFlow(args[3]);
	}// update Override

	public void resetRoundabout()
	{
		// Erase vehicles
		m_vehicles.clear();

		// Erase all stats
		m_goodTimes.clear();
		m_mediumTimes.clear();
		m_badTimes.clear();

		// Adjust the times
		m_simulationTime = 0;
		updateTimes();
	}

	/**
	 * Calculates where the vehicles should be in the next frame.
	 */
	public void calculate()
	{
		m_simulationTime += 1000 / RenderWindow.FPS;
		try
		{
			for (int i = 0; i < vehicleCount(); i++)
			{
				m_vehicles.get(i).calculate();
			}
			disposeVehicles();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;

		super.paintComponent(g2d);

		int halfWidth = getWidth() / 2;
		int halfHeight = getHeight() / 2;

		// Translates the canvas
		g2d.translate(halfWidth, halfHeight);

		// Clears everything
		g2d.clearRect(-halfWidth, -halfHeight, getWidth(), getHeight());

		m_radiusRoundaboutPixels = meterToPixel(getRadius());
		m_radiusCenterPixels = meterToPixel(getRadius() - 2 * Vehicle.WIDTH);

		// Draw the roundabout
		paintRoundabout(g2d);

		// Draw the vehicles
		paintVehicles(g2d);

		// Draw the traffic light
		m_light.paintTrafficLight(g2d);
	}// paintComponent Override

	/**
	 * Draws the roundabout accordingly with its current settings.
	 * 
	 * @param g
	 *            - Graphics
	 */
	private void paintRoundabout(Graphics g)
	{
		// Draw the lanes, if there are any.
		if (getNbLane() > 0)
		{

			Graphics2D g2d = (Graphics2D) g;

			// Draws the grass
			g2d.setColor(GRASS_COLOR);

			g2d.fillRect(-getWidth() / 2,			// x
					-getHeight() / 2,			// y
					getWidth(),				// width
					getHeight());			// height

			// Draws the lanes
			g2d.setColor(Color.GRAY);
			double stepAngle = 2 * Math.PI / getNbLane();
			m_laneWidthInPixel = (int) (2f * (m_radiusRoundaboutPixels - m_radiusCenterPixels));
			for (int i = 0; i < getNbLane(); i++)
			{
				g2d.rotate(stepAngle);
				g2d.fillRect(0, 											// x
						-m_laneWidthInPixel / 2,							// y
						(int) (Math.sqrt(Math.pow(getWidth(), 2) + Math.pow(getHeight(), 2)) / 2), 					// width
						m_laneWidthInPixel); 								// height
			}
		}

		// Draws the circular part of the roundabout.
		g.setColor(Color.GRAY);
		g.fillOval(-m_radiusRoundaboutPixels, -m_radiusRoundaboutPixels, 2 * m_radiusRoundaboutPixels, 2 * m_radiusRoundaboutPixels); // Extérieur

		g.setColor(GRASS_COLOR);
		g.fillOval(-m_radiusCenterPixels, -m_radiusCenterPixels, 2 * m_radiusCenterPixels, 2 * m_radiusCenterPixels); // Intérieur
	}// paintRoundabout

	/**
	 * Draws all simulated vehicles.
	 * 
	 * @param g
	 *            - Graphics
	 */
	private void paintVehicles(Graphics g)
	{
		try
		{
			for (int i = 0; i < vehicleCount(); i++)
			{
				m_vehicles.get(i).paintVehicle(g);
			}
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Generates a vehicle.
	 */
	public void generateVehicle()
	{
		m_vehicles.add(new Vehicle(this));
	}

	/**
	 * Disposes of vehicles that no longer need to be simulated.
	 */
	public void disposeVehicles()
	{
		for (int i = vehicleCount() - 1; i >= 0; i--)
		{
			if (meterToPixel(m_vehicles.get(i).getRadius()) >= Math.max(getWidth(), getHeight())
					&& m_vehicles.get(i).getState() == Vehicle.State.LEAVING)
			{
				manageLifeTimeData(m_vehicles.get(i).getLifeTime());
				m_vehicles.remove(i);
			}
		}
	}

	/**
	 * Adjusts the values of m_mediumTime and m_badTime.
	 */
	public void updateTimes()
	{
		m_mediumTime = (int) ((float) (pixelToMeter(Math.max(getWidth(), getHeight())) + Vehicle.LENGTH) / 15f * 3.6f);
		m_badTime = (int) ((float) (pixelToMeter(Math.max(getWidth(), getHeight())) + Vehicle.LENGTH) / 8f * 3.6f);
	}

	/**
	 * Receives the life time of a vehicle and stores it in its database.
	 * 
	 * @param lifeTime
	 *            in milliseconds - long
	 */
	private void manageLifeTimeData(long lifeTime)
	{
		lifeTime /= 1000;	// Transformer en secondes

		if (0 <= lifeTime && lifeTime < m_mediumTime)					// Bon temps
		{
			m_goodTimes.add(lifeTime);
		}
		else
			if (m_mediumTime <= lifeTime && lifeTime < m_badTime)			// Moyen Temps
			{
				m_mediumTimes.add(lifeTime);
			}
			else
				if (m_badTime <= lifeTime)								// Mauvais temps
				{
					m_badTimes.add(lifeTime);
				}
	}

	/**
	 * Receives circulation data and stores it.
	 * 
	 * @return: Vector of 3 Integers: 1st: Green 2nd: Yellow 3rd: Red
	 */
	public Vector<Integer> getCirculationStatisticalData()
	{
		Vector<Integer> data = new Vector<Integer>();

		data.add(m_goodTimes.size());
		data.add(m_mediumTimes.size());
		data.add(m_badTimes.size());

		return data;
	}

	/**
	 * Returns the average time it takes for a vehicle to go through the
	 * simulation.
	 * 
	 * @return seconds - int
	 */
	public int getAverageVehicleLifeTime()
	{
		int nbData = m_goodTimes.size() + m_mediumTimes.size() + m_badTimes.size();
		if (nbData == 0)
		{
			return 0;
		}

		int sum = 0;

		for (int i = 0; i < m_goodTimes.size(); i++)
		{
			sum += m_goodTimes.get(i);
		}

		for (int i = 0; i < m_mediumTimes.size(); i++)
		{
			sum += m_mediumTimes.get(i);
		}

		for (int i = 0; i < m_badTimes.size(); i++)
		{
			sum += m_badTimes.get(i);
		}

		int average = sum / nbData;

		return average;
	}

	public boolean checkInsideRoundabout(float theta)
	{
		return false;
	}// checkInsideRoundabout

	/**
	 * Verifies if the vehicle in its current state, radius and theta can be
	 * placed on the road. If it can the method returns true. If it can't it
	 * returns false.
	 * 
	 * @param vehicle
	 *            - Vehicle
	 * @return boolean
	 */
	public boolean canPlaceVehicle(Vehicle vehicle)
	{
		for (int i = 0; i < vehicleCount(); i++)
		{
			if (m_vehicles.get(i).getState() == Vehicle.State.ENTERING)
			{
				if (0.01 >= Math.abs(m_vehicles.get(i).getTheta() - vehicle.getTheta()))
				{
					float distance = vehicle.getRadius() - m_vehicles.get(i).getRadius();
					if ((distance - Vehicle.LENGTH) < 5.0f && distance > 0.0f)
					{
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Verifies for a specific vehicle if the way is clear in front of it. If
	 * the vehicle must stop, the method returns true. Otherwise it return
	 * false.
	 * 
	 * @param vehicle
	 *            - Vehicle
	 * @return boolean
	 */
	public boolean checkLane(Vehicle vehicle)
	{
		// Verifies clearance between vehicles to avoid collision
		for (int i = 0; i < vehicleCount(); i++)
		{
			if (!m_vehicles.get(i).equals(vehicle) && 0.01 >= Math.abs(m_vehicles.get(i).getTheta() - vehicle.getTheta()))
			{
				float distance = m_vehicles.get(i).getRadius();
				if (vehicle.getState() == m_vehicles.get(i).getState())
				{
					switch (vehicle.getState())
					{
						case ENTERING:
							distance = vehicle.getRadius() - distance;
							break;
						case LEAVING:
							distance -= vehicle.getRadius();
						default:
							break;
					}
				}
				if (!vehicle.isMoving() && (distance - Vehicle.LENGTH) < 5.0f && distance > 0.0f)
				{
					return true;
				}

				else
					if (distance - Vehicle.LENGTH < 2.0f && distance > 0.0f)
					{
						return true;
					}
			}

		}

		// Verifies if the vehicle must stop at the light
		if (m_light.getColor().equals(Color.RED) && (Math.cos(vehicle.getTheta()) >= Math.cos(Math.PI / 16)))
		{
			if (vehicle.getState() == Vehicle.State.ENTERING)
			{
				float dist = vehicle.getRadius() - Vehicle.LENGTH / 2 - m_light.getX();

				// If the car if 1 meter from the light or less, it stops
				if (0 <= dist && dist <= 1)
				{
					return true;
				} // Must stop
			}
		}

		return false;
	}// checkLane

	public boolean checkExit(Vehicle vehicle)
	{
		return false;
	}// checkExit

	/**
	 * Verifies for a specific vehicle if the way is clear to enter the inside
	 * of the roundabout. If the way is clear, the method returns false. Otherwise
	 * it return true.
	 * 
	 * @param vehicle
	 *            - Vehicle
	 * @return boolean
	 */
	public boolean checkEntrance(Vehicle vehicle)
	{
		for (int i = 0; i < vehicleCount(); i++)
		{
			if (m_vehicles.get(i).getState() == Vehicle.State.INSIDE)
			{
				float theta = m_vehicles.get(i).getTheta();
				float dist = getRadius() * (theta - vehicle.getTheta());
				float dist2 = getRadius() * (float) Math.abs(theta - vehicle.getTheta() + 2 * Math.PI);
				if ((dist - Vehicle.LENGTH <= 7.0f && dist + Vehicle.LENGTH >= -0.0f)
						|| (dist2 - Vehicle.LENGTH <= 7.0f && dist2 + Vehicle.LENGTH >= -0.0f)) return true;
			}
		}
		return false;
	}// checkEntrance

	// Getters
	public int getLaneWidthInPixel()
	{
		return m_laneWidthInPixel;
	}

	public int getStopTime()
	{
		return m_halfPeriod;
	}

	public int getRadius()
	{
		return m_radius;
	}

	public int getNbLane()
	{
		return m_nbLane;
	}

	public int getTimeFactor()
	{
		return m_timeFactor;
	}

	public int getMediumTime()
	{
		return m_mediumTime;
	}

	public int getBadTime()
	{
		return m_badTime;
	}

	public int getSimulationTime()
	{
		return m_simulationTime;
	}

	public int getCarFlow()
	{
		return m_carFlow;
	}

	/**
	 * Returns the size of the ArrayList of vehicles.
	 * 
	 * @return int
	 */
	public int vehicleCount()
	{
		return m_vehicles.size();
	}

	/**
	 * Returns the number of vehicle inside the roundabout.
	 * 
	 * @return int
	 */
	public int vehicleCountInside()
	{
		int count = 0;
		for (int i = 0; i < vehicleCount(); i++)
		{
			if (m_vehicles.get(i).getState() == Vehicle.State.INSIDE) count++;
		}
		return count;
	}

	// Setters
	public void setStopTime(int stopTime)
	{
		this.m_halfPeriod = stopTime;
	}

	/**
	 * Sets the value of m_carFlow. Unless the parameter is below or equal to
	 * zero, in that case, the method does nothing
	 * 
	 * @param carFlow
	 *            - int
	 */
	public void setCarFlow(int carFlow)
	{
		if (carFlow > 0) m_carFlow = carFlow;
	}

	public void setRadius(int radius)
	{
		this.m_radius = radius;
	}

	public void setNbLane(int nb_lane)
	{
		this.m_nbLane = nb_lane;
	}

	/**
	 * Sets the value of m_timeFactor. Then restart the traffic light timer.
	 * 
	 * @param timeFactor
	 *            - int
	 */
	public void setTimeFactor(int timeFactor)
	{
		m_timeFactor = timeFactor;
		m_light.restart();
	}

}// class Roundabout

