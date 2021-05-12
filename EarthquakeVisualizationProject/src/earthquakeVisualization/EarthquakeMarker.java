package earthquakeVisualization;

import processing.core.PConstants;
import processing.core.PGraphics;

import de.fhpotsdam.unfolding.data.PointFeature;

public abstract class EarthquakeMarker extends CommonMarker implements Comparable<EarthquakeMarker> {
	
	//to know if earthquake occur on land
	//this will be set by subclasses
	protected boolean isOnLand;
	
	//the radius of earthquake marker
	protected float radius;
	
	//constant for distance
	public static final float kmPerMiles = 1.6f;
	
	//thresholds for earthquake's magnitude
	public static final float THRESHOLD_MODERATE = 5;
	public static final float THRESHOLD_LIGHT = 4;
	
	//thresholds for earthquake's depth
	public static final float THRESHOLD_INTERMEDIATE = 70;
	public static final float THRESHOLD_DEEP = 300;
	
	//to draw different earthquakes (land or ocean)
	public abstract void drawEarthquake(PGraphics pg, float x, float y);
	
	public EarthquakeMarker(PointFeature feature)
	{
		super(feature.getLocation());
		
		//add a radius property and then set the properties
		java.util.HashMap<String, Object> properties = feature.getProperties();
		float magnitude = Float.parseFloat(properties.get("magnitude").toString());
		properties.put("radius", 2*magnitude);
		setProperties(properties);
		this.radius = 1.75f*getMagnitude();
	}
	
	public void drawMarker(PGraphics pg, float x, float y)
	{
		//save previous drawing style
		pg.pushStyle();
		
		//to colour the marker according to depth
		colorDetermine(pg);
		
		//will be implemented in subclasses
		drawEarthquake(pg, x, y);
		
		//add x over the markers which occurred within past day
		String age = getStringProperty("age");
		if("Past Day".equals(age))
		{
			pg.strokeWeight(2);
			int buffer = 2;
			pg.line(x-(radius+buffer), y-(radius+buffer), x+(radius+buffer), y+(radius+buffer));
			pg.line(x-(radius+buffer), y+(radius+buffer), x+(radius+buffer), y-(radius+buffer));
		}
		
		//restore previous drawing style
		pg.popStyle();
	}
	
	//show title of earthquake if this marker is selected
	//i.e. mouse is over the marker
	public void showTitle(PGraphics pg, float x, float y)
	{
		String title = getTitle();
		//save previous drawing style
		pg.pushStyle();
		
		pg.rectMode(PConstants.CORNER);
		
		pg.stroke(110);
		pg.fill(255,255,255);
		pg.rect(x, y + 15, pg.textWidth(title) + 6, 18, 5);
		
		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.fill(0);
		pg.text(title, x + 3 , y +18);
		
		//restore previous drawing style
		pg.popStyle();
	}
	
	//to get distance upto which this earthquake can affect things
	public double threatCircle()
	{
		double miles = 20.0f * Math.pow(1.8, 2*getMagnitude() - 5);
		double km = miles * kmPerMiles;
		return km;
	}
	
	//determine colour of the marker
	private void colorDetermine(PGraphics pg)
	{
		float depth = getDepth();
		
		if(depth < THRESHOLD_INTERMEDIATE)
		{
			pg.fill(255, 255, 0);
		}
		else if(depth < THRESHOLD_DEEP)
		{
			pg.fill(0, 0, 255);
		}
		else
		{
			pg.fill(255, 0, 0);
		}
	}
	
	//to compare 2 objects of EarthquakeMarker class
	//overriding the method from Comparable interface
	//usually the smaller.compareTo(bigger) returns -1
	//but we want descending order so we reversed it
	public int compareTo(EarthquakeMarker marker)
	{
		if(getMagnitude() < marker.getMagnitude())
		{
			return 1;
		}
		else if(getMagnitude() == marker.getMagnitude())
		{
			return 0;
		}
		else
		{
			return -1;
		}
	}
	
	
	//getters for earthquake properties
	public float getMagnitude() {
		return Float.parseFloat(getProperty("magnitude").toString());
	}
	
	public float getDepth() {
		return Float.parseFloat(getProperty("depth").toString());	
	}
	
	public String getTitle() {
		return (String) getProperty("title");	
		
	}
	
	public float getRadius() {
		return Float.parseFloat(getProperty("radius").toString());
	}
	
	public boolean isOnLand()
	{
		return isOnLand;
	}
	
	//returns string representation of the object
	public String toString()
	{
		return getTitle();
	}
}
