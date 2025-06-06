
package acme.features.administrator.airline;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airline.Airline;
import acme.entities.airline.AirlineType;

@GuiService
public class AdministratorAirlineShowService extends AbstractGuiService<Administrator, Airline> {

	@Autowired
	private AdministratorAirlineRepository repository;


	@Override
	public void authorise() {
		boolean authorised = true;

		if (super.getRequest().hasData("id")) {
			int id = super.getRequest().getData("id", int.class);
			Airline airline = this.repository.findAirlineById(id);
			if (airline == null)
				authorised = false;
		} else
			authorised = false;
		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int id;
		Airline airline;

		id = super.getRequest().getData("id", int.class);
		airline = this.repository.findAirlineById(id);
		super.getBuffer().addData(airline);
	}

	@Override
	public void unbind(final Airline airline) {
		Dataset dataset;
		SelectChoices typeChoices;
		typeChoices = SelectChoices.from(AirlineType.class, airline.getType());

		dataset = super.unbindObject(airline, "name", "iataCode", "websiteUrl", "type", "phoneNumber", "email", "foundationMoment");
		dataset.put("types", typeChoices);

		super.getResponse().addData(dataset);
	}

}
