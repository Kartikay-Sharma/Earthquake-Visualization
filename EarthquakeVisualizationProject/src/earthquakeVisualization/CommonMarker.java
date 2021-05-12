package earthquakeVisualization;

import processing.core.PGraphics;

import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.geo.Location;

public abstract class CommonMarker extends SimplePointMarker {
	
	//Records whether this marker has been clicked recently
	protected boolean clicked = false;
	
	public CommonMarker(Location location)
	{
		super(location);
	}
	
	public CommonMarker(Location location, java.util.HashMap<java.lang.String, java.lang.Object> properties)
	{
		super(location, properties);
	}
	
	//to customize markers instead of displaying default markers
	//drawMarker and showTitle will be implemented in subclasses
	public void draw(PGraphics pg, float x, float y)
	{
		if(!hidden)
		{
			drawMarker(pg, x, y);
			if(selected)
			{
				showTitle(pg, x, y);
			}
		}
	}
	
	//to draw different markers (city or earthquake)
	public abstract void drawMarker(PGraphics pg, float x, float y);
	//to show title of markers
	public abstract void showTitle(PGraphics pg, float x, float y);
	
	
	//setters and getters
	public boolean getClicked()
	{
		return clicked;
	}
	
	public void setClicked(boolean state)
	{
		clicked = state;
	}
}
