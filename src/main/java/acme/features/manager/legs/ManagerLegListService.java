
package acme.features.manager.legs;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flight.Flight;
import acme.entities.legs.Leg;
import acme.features.manager.flights.ManagerFlightRepository;
import acme.realms.manager.Manager;

@GuiService
public class ManagerLegListService extends AbstractGuiService<Manager, Leg> {

	@Autowired
	private ManagerLegRepository	repository;

	@Autowired
	private ManagerFlightRepository	flightRepository;


	@Override
	public void authorise() {
		boolean status = false;

		if (super.getRequest().hasData("flightId", int.class)) {
			int flightId = super.getRequest().getData("flightId", int.class);
			int managerId = super.getRequest().getPrincipal().getActiveRealm().getId();

			if (this.flightRepository.findFlightById(flightId) != null && this.repository.findByIdAndManagerId(flightId, managerId).isPresent())
				status = true;
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int flightId;

		flightId = super.getRequest().getData("flightId", int.class);
		List<Leg> legs = this.repository.findAllLegsByFlightId(flightId);
		legs.sort(Comparator.comparing(Leg::getScheduledDeparture));

		super.getBuffer().addData(legs);
	}

	@Override
	public void unbind(final Leg leg) {
		Dataset dataset;

		dataset = super.unbindObject(leg, "flightNumber", "scheduledDeparture");
		dataset.put("departureAirport", leg.getDepartureAirport().getName());
		dataset.put("arrivalAirport", leg.getArrivalAirport().getName());
		dataset.put("flight", leg.getFlight());

		super.getResponse().addData(dataset);
	}

	@Override
	public void unbind(final Collection<Leg> legs) {
		Integer flightId = super.getRequest().getData("flightId", int.class);
		super.getResponse().addGlobal("flightId", flightId);

		Flight flight = this.repository.findFlightByFlightId(flightId);
		super.getResponse().addGlobal("flightDraftMode", flight.isDraftMode());
	}

}
