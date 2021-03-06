package edu.kastel.smarthome.system;

import edu.kastel.smarthome.repository.interfaces.ConsumptionMonitoring;
import edu.kastel.smarthome.repository.interfaces.MeterReading;
import edu.kastel.smarthome.repository.interfaces.RelaisControlling;
import edu.kastel.smarthome.repository.interfaces.TabletControlling;

public class SmartHome implements TabletControlling, ConsumptionMonitoring {
	@SuppressWarnings("unused")
	private MeterReading meterReading;
	@SuppressWarnings("unused")
	private RelaisControlling relaisControlling;
	private TabletControlling tabletControlling;
	private ConsumptionMonitoring consumptionMonitoring;

	public SmartHome(MeterReading meterReading, RelaisControlling relaisControlling, TabletControlling tabletControlling, ConsumptionMonitoring consumptionMonitoring) {
		this.meterReading = meterReading;
		this.relaisControlling = relaisControlling;
		this.tabletControlling = tabletControlling;
		this.consumptionMonitoring = consumptionMonitoring;
	}

	@Override
	public int getEnergyConsumption() {
		return this.consumptionMonitoring.getEnergyConsumption();
	}

	@Override
	public int switchOn(int id) {
		return this.tabletControlling.switchOn(id);
	}
    
	@Override
	public int getConsumption(boolean present) {
		return this.tabletControlling.getConsumption(present);
	}
	
	@Override
	public void setPresence(boolean present) {
		this.tabletControlling.setPresence(present);
	}
}
