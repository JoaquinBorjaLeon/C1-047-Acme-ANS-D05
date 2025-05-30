
package acme.entities.flight;

import java.beans.Transient;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.springframework.context.i18n.LocaleContextHolder;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoney;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidFlight;
import acme.entities.airport.Airport;
import acme.entities.legs.Leg;
import acme.realms.manager.Manager;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidFlight
@Table(indexes = {
	@Index(columnList = "manager_id")
})
public class Flight extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@Size(min = 1, max = 50)
	@Automapped
	private String				tag;

	@Mandatory
	@Automapped
	@Valid
	private Boolean				requiresSelfTransfer;

	@Mandatory
	@ValidMoney(min = 0.01)
	@Automapped
	private Money				cost;

	@Optional
	@Size(max = 255)
	@Automapped
	private String				description;

	@Mandatory
	@Automapped
	private boolean				draftMode;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Manager				manager;


	@Transient
	public Date getFlightDeparture() {
		FlightRepository repository = SpringHelper.getBean(FlightRepository.class);
		List<Leg> listOfLegs = repository.legsDuringFlight(this.getId());
		Date scheduledDeparture = null;
		if (!listOfLegs.isEmpty()) {
			Leg firstLeg = null;
			for (Leg leg : listOfLegs)
				if (firstLeg == null || leg.getScheduledDeparture().before(firstLeg.getScheduledDeparture()))
					firstLeg = leg;
			if (firstLeg != null)
				scheduledDeparture = firstLeg.getScheduledDeparture();
		}
		return scheduledDeparture;
	}

	@Transient
	public Date getFlightArrival() {
		FlightRepository repository = SpringHelper.getBean(FlightRepository.class);
		List<Leg> listOfLegs = repository.legsDuringFlight(this.getId());
		Date scheduledArrival = null;
		if (!listOfLegs.isEmpty()) {
			Leg lastLeg = null;
			for (Leg leg : listOfLegs)
				if (lastLeg == null || leg.getScheduledArrival().after(lastLeg.getScheduledArrival()))
					lastLeg = leg;
			if (lastLeg != null)
				scheduledArrival = lastLeg.getScheduledArrival();
		}
		return scheduledArrival;
	}

	@Transient
	public Integer getLayovers() {
		FlightRepository repository = SpringHelper.getBean(FlightRepository.class);
		List<Leg> listOfLegs = repository.legsDuringFlight(this.getId());
		return Math.max(0, listOfLegs.size() - 1);
	}

	@Transient
	public Airport getDeparture() {
		FlightRepository repository = SpringHelper.getBean(FlightRepository.class);
		List<Leg> listOfLegs = repository.legsDuringFlight(this.getId());
		Leg firstLeg = null;
		if (!listOfLegs.isEmpty())
			for (Leg leg : listOfLegs)
				if (firstLeg == null || leg.getScheduledDeparture().before(firstLeg.getScheduledDeparture()))
					firstLeg = leg;
		return firstLeg != null ? firstLeg.getDepartureAirport() : null;
	}

	@Transient
	public Airport getArrival() {
		FlightRepository repository = SpringHelper.getBean(FlightRepository.class);
		List<Leg> listOfLegs = repository.legsDuringFlight(this.getId());
		Leg lastLeg = null;
		if (!listOfLegs.isEmpty())
			for (Leg leg : listOfLegs)
				if (lastLeg == null || leg.getScheduledArrival().after(lastLeg.getScheduledArrival()))
					lastLeg = leg;
		return lastLeg != null ? lastLeg.getArrivalAirport() : null;
	}

	@Transient
	public String getInfo() {
		String res = "-";
		Date depDate = this.getFlightDeparture();
		Date arrDate = this.getFlightArrival();
		Airport depAirport = this.getDeparture();
		Airport arrAirport = this.getArrival();

		Locale locale = LocaleContextHolder.getLocale();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);
		String formattedDep = dateFormat.format(depDate);
		String formattedArr = dateFormat.format(arrDate);

		if (formattedDep != null && formattedArr != null && depAirport != null && arrAirport != null)
			res = String.format("%s %s -> %s %s", formattedDep, depAirport.getCity(), formattedArr, arrAirport.getCity());
		return res;
	}

}
