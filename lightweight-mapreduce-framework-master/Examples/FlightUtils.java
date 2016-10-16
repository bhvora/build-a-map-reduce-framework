public class FlightUtils {
	/*
	Method to enforce the sanity test for every row (flight data).
	Returns: Boolean value depending on whether a flight passes
	sanity test or not.
	 */
	public static boolean sanityTest(String[] row){

		//System.out.println(row.length);

		if(row.length != 110){
			return false;
		}
		try {
			int year = Integer.parseInt(row[0]);
			if(year < 2010 || year > 2015 ){
				return false;
			}

			//Time is converted to minutes
			int CRSArrTime = row[40].isEmpty()?0:convertTime(row[40]);
			int CRSDepTime = row[29].isEmpty()?0:convertTime(row[29]);
			int CRSElapsedTime = (int) Float.parseFloat(row[50]);
			int timeZone = CRSArrTime - CRSDepTime - CRSElapsedTime;

			//CRSArrTime and CRSDepTime should not be zero

			if (CRSArrTime == 0 || CRSDepTime == 0) {
				return false;
			}	

			//timeZone % 60 should be 0
			if (timeZone % 60 != 0) {
				return false;
			}

			int OriginAirportId = (int) Float.parseFloat(row[11]);
			int DestAirportId = (int) Float.parseFloat(row[20]);
			int OriginAirportSeqId = (int) Float.parseFloat(row[12]);
			int DestAirportSeqId = (int) Float.parseFloat(row[21]);
			int OriginCityMarketId = (int) Float.parseFloat(row[13]);
			int DestCityMarketId = (int) Float.parseFloat(row[22]);
			int OriginStateFips = (int) Float.parseFloat(row[17]);
			int DestStateFips = (int) Float.parseFloat(row[26]);
			int OriginWac = (int) Float.parseFloat(row[19]);
			int DestWac = (int) Float.parseFloat(row[28]);

			//AirportID,  AirportSeqID, CityMarketID, StateFips, Wac should be larger than 0
			if (OriginAirportId <= 0 || DestAirportId <= 0 || OriginAirportSeqId <= 0 || 
					DestAirportSeqId <= 0 || OriginCityMarketId <= 0 || DestCityMarketId <= 0 || 
					OriginStateFips <= 0 || DestStateFips <= 0 || OriginWac <= 0 || DestWac <= 0) {
				return false;
			}

			//Origin, Destination,  CityName, State, StateName should not be empty
			if (row[14].isEmpty() || row[23].isEmpty() || row[15].isEmpty() || row[24].isEmpty() ||
					row[16].isEmpty() || row[25].isEmpty() || row[18].isEmpty() || row[27].isEmpty()) {
				return false;
			}

			//Flights that are not Cancelled
			int cancelled = (int) Float.parseFloat(row[47]);
			if (cancelled != 1) {
				int arrTime = row[41].isEmpty()?0:convertTime(row[41]);
				int depTime = row[30].isEmpty()?0:convertTime(row[30]);
				int actualElapsedTime = (int) Float.parseFloat(row[51]);

				//ArrTime -  DepTime - ActualElapsedTime - timeZone should be zero
				int diff = arrTime - depTime - actualElapsedTime - timeZone;
				if (diff != 0 && diff % 1440 != 0) {
					return false;
				}

				int arrDelay = (int) Float.parseFloat(row[42]);
				int arrDelayMinutes = (int) Float.parseFloat(row[43]);

				//if ArrDelay > 0 then ArrDelay should equal to ArrDelayMinutes
				//if ArrDelay < 0 then ArrDelayMinutes should be zero
				if (arrDelay > 0 && arrDelay != arrDelayMinutes) {
					return false;
				} else if (arrDelay < 0 && arrDelayMinutes != 0) {
					return false;
				}

				//if ArrDelayMinutes >= 15 then ArrDel15 should be true
				if (arrDelayMinutes >= 15 && ((int) Float.parseFloat(row[44])) != 1) {
					return false;
				}
			}
		} catch (NumberFormatException exception) {
			return false;
		}
		return true;		
	}

	/**
	 * Method to convert time in hh:mm format to Minutes.
	 * @param String s (time in hh:mm)
	 * @return int    time in minutes
	 */
	public  static int convertTime(String s){

		String result = appendString(s);

		int time = 0;

		if(result.length() == 4){
			int hours = Integer.parseInt(result.substring(0,2));
			int minutes = Integer.parseInt(result.substring(2,4));

			time = hours * 60 + minutes;

		}
		return time;
	}

	/**
	 * Method to append number of zeros to the time based on its length
	 * @param String s (time in hh:mm)
	 * @return String appended string with zeros.
	 */
	public static String appendString(String s){

		String result = "";
		String s_new = s.trim();
		if(s_new.length()< 4 && !s_new.isEmpty()){
			result = "0" + s_new;
		}
		else {
			result = s_new;
		}

		return result;
	}
}
