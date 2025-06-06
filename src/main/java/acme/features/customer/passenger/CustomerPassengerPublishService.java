
package acme.features.customer.passenger;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Passenger;
import acme.realms.Customer;

@GuiService
public class CustomerPassengerPublishService extends AbstractGuiService<Customer, Passenger> {

	@Autowired
	private CustomerPassengerRepository repository;


	@Override
	public void authorise() {
		boolean authorised;
		int passengerId;
		Passenger passenger;
		Customer customer;

		if (super.getRequest().hasData("id")) {
			passengerId = super.getRequest().getData("id", int.class);
			passenger = this.repository.findPassengerById(passengerId);
			customer = passenger == null ? null : passenger.getCustomer();
			authorised = customer != null && passenger.isDraftMode() && super.getRequest().getPrincipal().hasRealm(customer);
		} else
			authorised = false;

		super.getResponse().setAuthorised(authorised);
	}

	@Override
	public void load() {
		int passengerId;
		Passenger passenger;

		passengerId = super.getRequest().getData("id", int.class);
		passenger = this.repository.findPassengerById(passengerId);

		super.getBuffer().addData(passenger);

	}

	@Override
	public void bind(final Passenger passenger) {
		super.bindObject(passenger, "fullName", "email", "passport", "birthDate", "specialNeeds");
	}

	@Override
	public void perform(final Passenger passenger) {
		passenger.setDraftMode(false);
		this.repository.save(passenger);
	}

	@Override
	public void validate(final Passenger passenger) {
		int passengerId;
		boolean existsDuplicatedPassport;

		passengerId = super.getRequest().getData("id", int.class);
		int customerId = passenger.getCustomer().getId();
		existsDuplicatedPassport = this.repository.existsPassengerWithDuplicatedPassport(passenger.getPassport(), customerId, passengerId);
		super.state(!existsDuplicatedPassport, "passport", "acme.validation.confirmation.passenger.passport");
	}

	@Override
	public void unbind(final Passenger passenger) {
		Dataset dataset;
		dataset = super.unbindObject(passenger, "fullName", "email", "passport", "birthDate", "specialNeeds", "draftMode");
		super.getResponse().addData(dataset);

	}

}
