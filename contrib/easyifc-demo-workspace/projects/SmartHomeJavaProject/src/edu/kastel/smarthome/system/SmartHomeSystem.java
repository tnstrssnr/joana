package edu.kastel.smarthome.system;

import edu.kastel.smarthome.repository.components.ApplianceDriver;
import edu.kastel.smarthome.repository.components.EnergyCenter;
import edu.kastel.smarthome.repository.components.SmartMeter;
import edu.kastel.smarthome.repository.components.Tablet;
import edu.kastel.smarthome.repository.interfaces.ApplianceControlling;
import edu.kastel.smarthome.repository.interfaces.ConsumptionMonitoring;
import edu.kastel.smarthome.repository.interfaces.EnergyControlling;
import edu.kastel.smarthome.repository.interfaces.MeterReading;
import edu.kastel.smarthome.repository.interfaces.RelaisControlling;
import edu.kastel.smarthome.repository.interfaces.SmartMeterReading;
import edu.kastel.smarthome.repository.interfaces.TabletControlling;

public class SmartHomeSystem {

	public static void main(String[] args) {
		MeterReading meterReading = new MeterReading() {
			
			@Override
			public int getEnergyCount() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
		RelaisControlling relaisControlling = new RelaisControlling() {
			
			@Override
			public int toggle() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getState() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
		SmartMeterReading smartMeterReading = new SmartMeter(meterReading);
		ApplianceControlling applianceControlling = new ApplianceDriver(relaisControlling);
		EnergyCenter energyCenter = new EnergyCenter(smartMeterReading);
		energyCenter.addAppliance(applianceControlling);
		TabletControlling tabletControlling = new Tablet((EnergyControlling) energyCenter);
		ConsumptionMonitoring consumptionMonitoring = (ConsumptionMonitoring) energyCenter;
		@SuppressWarnings("unused")
		SmartHome smartHome = new SmartHome(meterReading, relaisControlling, tabletControlling, consumptionMonitoring);
		// ready steady go
	}
}
