package earthquakeVisualization;

import processing.core.PGraphics;

import de.fhpotsdam.unfolding.data.PointFeature;

public class OceanQuakeMarker extends EarthquakeMarker {
	
	public OceanQuakeMarker(PointFeature quake)
	{
		super(quake);
		isOnLand = false;
	}
	
	//to draw OceanQuake marker
	public void drawEarthquake(PGraphics pg, float x, float y)
	{
		//drawing our own marker for ocean quake
		pg.rect(x-radius, y-radius, 2*radius, 2*radius);
	}
}
