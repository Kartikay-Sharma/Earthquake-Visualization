package earthquakeVisualization;

import processing.core.PGraphics;

import de.fhpotsdam.unfolding.data.PointFeature;

public class LandQuakeMarker extends EarthquakeMarker {
	
	public LandQuakeMarker(PointFeature quake)
	{
		super(quake);
		isOnLand = true;
	}
	
	//to draw LandQuake marker
	public void drawEarthquake(PGraphics pg, float x, float y)
	{
		//drawing our own marker for land quake
		pg.ellipse(x, y, 2*radius, 2*radius);
	}
	
	
	//get country the earthquake is in
	public String getCountry()
	{
		return getProperty("country").toString();
	}
}
