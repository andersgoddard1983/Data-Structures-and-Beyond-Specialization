package bonus1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;

public class LifeExpectancy extends PApplet {
	
	UnfoldingMap map;
	Map<String, Float> lifeExpectancyByCountry;
	List<Feature> countries;
	List<Marker> countryMarkers;
	
	public void setup(){
		
		size(800, 600, OPENGL);
		map = new UnfoldingMap(this, 50, 50, 700, 500, new Microsoft.HybridProvider());
		MapUtils.createDefaultEventDispatcher(this, map);
		
		lifeExpectancyByCountry = loadLifeExpectancyFromCSV("data/life-expectancy-at-birth.csv");
		
		countries = GeoJSONReader.loadData(this, "data/countries.geo.json");
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		map.addMarkers(countryMarkers);
		
		shadeCountries();
		
	}
	
	public void draw(){
		map.draw();
	}
	
	private Map<String, Float> loadLifeExpectancyFromCSV(String filename){
		Map<String, Float> lifeExpectancyMap = new HashMap<String, Float>();
		String[] rows = loadStrings(filename);
		
		for (String row : rows){
			String[] columns = row.split(",");
			if (columns.length == 5 && !columns[4].equals("..")){
				float value = Float.parseFloat(columns[4]);
				lifeExpectancyMap.put(columns[1], value);
			}
		}
		
		
		
		return lifeExpectancyMap;
		
	}
	
	private void shadeCountries(){
		for (Marker marker : countryMarkers){
			String countryId = marker.getId();
			if (lifeExpectancyByCountry.containsKey(countryId)){
				float lifeExpectancy = lifeExpectancyByCountry.get(countryId);
				int colorLevel = (int) map(lifeExpectancy, 40, 90, 10, 255);
				marker.setColor(color(255-colorLevel, 100, colorLevel));
			} else {
				marker.setColor(color(150, 150, 150));				
			}
			
		}
	}
		
	public static void main (String... args) {
        LifeExpectancy expectancy = new LifeExpectancy();
        PApplet.runSketch(new String[]{"Life Expectancy"}, expectancy);
    }	

}