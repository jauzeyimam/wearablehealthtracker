package ee364h.wearablehealthtracker;

/**
 * Created by Nadeem on 3/17/2015.
 * Types of graphs for the app, used to communicate button pressed in HomePageFragment to
 * the visualizations of the GraphFragment
 */
public enum GraphType {

    PEDOMETER("Pedometer", -1, "Steps", "steps") {
        public Number getValueFromStringArray (String[]values){
            return null;
        }
    },
    PULSE("Pulse",1,"Heart Rate", "bpm"){
    	public Number getValueFromStringArray(String [] values){
        	return Integer.valueOf(values[this.getValueLocation()]);
    	}
    },
    BLOODOX("BloodOx",2,"Oxygen Saturation","%"){
	    public Number getValueFromStringArray(String [] values){
	        return Integer.valueOf(values[this.getValueLocation()]);
    	}
    },
    TEMPERATURE("Temperature",3,"Temperature","degrees Celsius"){
	    public Number getValueFromStringArray(String [] values){
	        return Double.valueOf(values[this.getValueLocation()]);
    	}
    };
    
    abstract public Number getValueFromStringArray(String[] values);

    private String name;
    private int valueLocation;
    private String title;
    private String units;

    private GraphType(String name, int valueLocation, String title, String units){
    	this.name = name;
    	this.valueLocation = valueLocation;
    	this.title = title;
    	this.units = units;
    }
    public String getName(){
    	return this.name;
    }
    public int getValueLocation(){
    	return this.valueLocation;
    }
    public String getTitle(){
    	return this.title;
    }
    public String getUnits(){
    	return this.units;
    }
}
