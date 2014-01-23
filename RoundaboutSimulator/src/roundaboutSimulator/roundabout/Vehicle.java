package roundaboutSimulator.roundabout;

// import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import roundaboutSimulator.ui.RenderWindow;


public class Vehicle
{
	public enum State
	{
		ENTERING, INSIDE, LEAVING
	};

	public static final float		LENGTH		= 4.6f;			// Meters
	public static final float		WIDTH		= 1.9f;			// Meters
	public static final float		SPEED		= 30;				// kilometers
																	// per hour
	private static BufferedImage	m_image;

	private Roundabout					m_roundabout;

	private State					m_state		= State.ENTERING;

	private float					m_theta		= 0;
	private float					m_radius	= 0;
	private int						m_source;
	private int						m_destination;
	private boolean					m_isMoving	= false;

	private int						m_lifeTime	= 0;				// Milliseconds

	/**
	 * Constructor. A vehicle requires a Roundabout to display correctly.
	 * 
	 * @param roundabout
	 */
	public Vehicle(Roundabout roundabout)
	{
		m_roundabout = roundabout;
		randomSource();
		randomDestination();
		setTheta();
		setRadius();
	}// Constructor

	/**
	 * Associate this vehicle with a random source lane.
	 */
	protected void randomSource()
	{
		m_source = (int) (m_roundabout.getNbLane() * Math.random());
	}// randomSource

	/**
	 * Associate this vehicle with a random destination lane.
	 */
	protected void randomDestination()
	{
		m_destination = (int) (m_roundabout.getNbLane() * Math.random());
	}// randomDirection

	/**
	 * Load a common image used by all vehicles.
	 */
	public static void loadImage()
	{
		try
		{
			m_image = ImageIO.read(new File("image/blue_car.png"));
		}
		catch (IOException ex)
		{
			System.out.println("Erreur: Les images n'ont pas pu ouvrir!");
		}
	}// loadImage

	/**
	 * Calculate the next position of a vehicle and move it.
	 */
	public void calculate()
	{
		checkRoad();
		if (isMoving())
		{
			moveVehicle();
		}
		m_lifeTime += 1000 / RenderWindow.FPS;
	}

	/**
	 * Paint vehicle with the correct rotation angle and position on a Graphics
	 * object
	 * 
	 * @param g
	 *            Graphics
	 */
	public void paintVehicle(Graphics g)
	{
		// Afficher les vehicules avec la bonne orientation
		Graphics2D g2d = (Graphics2D) g;

		int dX1 = 0, dY1 = 0, dX2 = 0, dY2 = 0;
		double rotationAngle = 0;

		switch (getState())
		{
			case ENTERING:
				rotationAngle = getTheta();
				dX1 = m_roundabout.meterToPixel(m_radius + Vehicle.LENGTH / 2);
				dY1 = m_roundabout.meterToPixel(-Vehicle.WIDTH / 2) + laneAdjustement();
				dX2 = m_roundabout.meterToPixel(m_radius - Vehicle.LENGTH / 2);
				dY2 = m_roundabout.meterToPixel(Vehicle.WIDTH / 2) + laneAdjustement();

				break;
			case LEAVING:
				rotationAngle = getTheta();
				dX1 = m_roundabout.meterToPixel(m_radius - Vehicle.LENGTH / 2);
				dY1 = m_roundabout.meterToPixel(-Vehicle.WIDTH / 2) + laneAdjustement();
				dX2 = m_roundabout.meterToPixel(m_radius + Vehicle.LENGTH / 2);
				dY2 = m_roundabout.meterToPixel(Vehicle.WIDTH / 2) + laneAdjustement();

				break;
			case INSIDE:
				rotationAngle = getTheta() - Math.PI / 2.0f;
				dX1 = m_roundabout.meterToPixel(-Vehicle.LENGTH / 2) + laneAdjustement();
				dY1 = m_roundabout.meterToPixel(m_radius - Vehicle.WIDTH / 2);
				dX2 = m_roundabout.meterToPixel(Vehicle.LENGTH / 2) + laneAdjustement();
				dY2 = m_roundabout.meterToPixel(m_radius + Vehicle.WIDTH / 2);

				break;
		}

		// Dessiner l'image du Vehicle correctement
		g2d.rotate(rotationAngle);
		g2d.drawImage(m_image, dX1, dY1, dX2, dY2, 0, 0, m_image.getWidth(), m_image.getHeight(), null);
		g2d.rotate(-rotationAngle);

	}// paintVehicle

	/**
	 * Moves the vehicle at the correct position depending on its state and
	 * currect position.
	 */
	protected void moveVehicle()
	{

		/*
		 * VERY IMPORTANT CHANGE VALUES ---> TO ADJUST SPEED
		 */
		float deplacement = SPEED / (float) RenderWindow.FPS / 3.6f;
		switch (getState())
		{
			case ENTERING:
				if (getRadius() <= m_roundabout.getRadius() - Vehicle.WIDTH) setState(Vehicle.State.INSIDE);
				setRadius(getRadius() - deplacement);
				break;
			case LEAVING:
				setRadius(getRadius() + deplacement);
				break;
			case INSIDE:
				if (isAtExit()) setState(Vehicle.State.LEAVING);
				break;
		}
		setTheta();
	}

	/**
	 * Move only if nothing blocks the vehicle's path
	 */
	protected void checkRoad()
	{
		switch (getState())
		{

			case ENTERING:
				if (getRadius() <= m_roundabout.getRadius() + 2 * Vehicle.WIDTH && getRadius() >= m_roundabout.getRadius() + Vehicle.WIDTH
						&& m_roundabout.checkEntrance(this))
				{
					stop();
					return;
				}
				// Pas de break, on exécute les instructions ci-dessous dans les
				// deux cas
				// Dans les deux cas:
			case LEAVING:
				if (m_roundabout.checkLane(this))	// Si besoin de freiner
					stop();
				else
					move();
				break;

			case INSIDE:
				if ((isAtExit() && m_roundabout.checkExit(this)) || m_roundabout.checkInsideRoundabout(getTheta()))
					stop();
				else
					move();
				break;
		}
	}

	/**
	 * Check if vehicle has reached its destination lane.
	 * 
	 * @return true if vehicle has reached its destination lane
	 */
	protected boolean isAtExit()
	{
		float diff;
		if (m_source <= m_destination)
			diff = getTheta() - Vehicle.LENGTH / m_roundabout.getRadius() + (float) (2f * Math.PI)
					- (float) (2f * Math.PI * m_destination / m_roundabout.getNbLane());
		else
			diff = getTheta() - Vehicle.LENGTH / m_roundabout.getRadius() - (float) (2f * Math.PI * m_destination / m_roundabout.getNbLane());

		if (diff <= 0)
		{
			return true;
		}
		return false;
	}

	/**
	 * Adjust vehicle's position to put it on the right side of the road
	 * 
	 * @return
	 */
	public int laneAdjustement()
	{
		switch (getState())
		{
			case ENTERING:
				return -m_roundabout.meterToPixel(Vehicle.WIDTH);
			case LEAVING:
				return m_roundabout.meterToPixel(Vehicle.WIDTH);
			case INSIDE:
			default:
				return 0;
		}
	}

	private void move()
	{
		m_isMoving = true;
	}

	private void stop()
	{
		m_isMoving = false;
	}

	// Getters
	public int getLifeTime()
	{
		return m_lifeTime;
	}

	public float getTheta()
	{
		return m_theta;
	}

	public float getRadius()
	{
		return m_radius;
	}

	public State getState()
	{
		return m_state;
	}

	public boolean isMoving()
	{
		return m_isMoving;
	}

	public int getSource()
	{
		return m_source;
	}

	public int getDirection()
	{
		return m_destination;
	}

	// Setters
	/**
	 * Automatically sets theta to a valid value.
	 */
	protected void setTheta()
	{
		switch (getState())
		{
			case ENTERING:
				m_theta = (float) (2f * Math.PI * m_source / m_roundabout.getNbLane());
				break;
			case LEAVING:
				m_theta = (float) (2f * Math.PI * m_destination / m_roundabout.getNbLane());
				break;
			case INSIDE:
				setRadius(m_roundabout.getRadius() - Vehicle.WIDTH);
				m_theta -= (SPEED / (float) RenderWindow.FPS / 3.6f / (float) getRadius());
				break;
		}
	}

	/**
	 * Automatically sets the radius to a valid value.
	 */
	protected void setRadius()
	{
		setRadius(m_roundabout.pixelToMeter(Math.max(m_roundabout.getWidth(), m_roundabout.getHeight()) / 2) + Vehicle.LENGTH);

		// Si on ne peut pas placer le vehicle, on le recule un peu
		while (!m_roundabout.canPlaceVehicle(this))
		{
			setRadius(getRadius() + 2 * Vehicle.LENGTH);
		}
	}// setRadius (method for the constructor)

	protected void setRadius(float radius)
	{
		this.m_radius = radius;
	}// setRadius

	protected void setState(State state)
	{
		this.m_state = state;
	}

}// class Vehicle
