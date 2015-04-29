package ee364h.wearablehealthtracker;

/**
 * Created by Nadeem on 3/17/2015.
 * Types of graphs for the app, used to communicate button pressed in HomePageFragment to
 * the visualizations of the GraphFragment
 */
public enum GraphType {

    PEDOMETER("Pedometer", 1, "Steps", "Steps") {
        public Number getValueFromStringArray (String[]values){
            return Integer.valueOf(values[this.getValueLocation()]);
        }
    },
    PULSE("Pulse",1,"Heart Rate", "BPM"){
    	public Number getValueFromStringArray(String [] values){
        	return Integer.valueOf(values[this.getValueLocation()]);
    	}
    },
    BLOODOX("BloodOx",2,"Saturation","%"){
	    public Number getValueFromStringArray(String [] values){
	        return Integer.valueOf(values[this.getValueLocation()]);
    	}
    },
    TEMPERATURE("Temperature",3,"Temperature","Degrees Celsius"){
	    public Number getValueFromStringArray(String [] values){
	        return Double.valueOf(values[this.getValueLocation()]);
    	}
    };
    
    abstract public Number getValueFromStringArray(String[] values);

    private final static String DATA_FILENAME = "HealthTrackerData.txt";
    private final static String PEDOMETER_FILENAME = "PedometerData.txt";

    private String name;
    private int valueLocation;
    private String title;
    private String units;
    private String filename;

    private GraphType(String name, int valueLocation, String title, String units){
    	this.name = name;
    	this.valueLocation = valueLocation;
    	this.title = title;
    	this.units = units;
        if(this.name.equals("Pedometer")) {
            this.filename = PEDOMETER_FILENAME;
        }else{
            this.filename = DATA_FILENAME;
        }
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
    public String getFilename(){return this.filename;}
}
