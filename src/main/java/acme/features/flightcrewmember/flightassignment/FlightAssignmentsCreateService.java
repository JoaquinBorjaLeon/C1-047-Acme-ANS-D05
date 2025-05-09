
package acme.features.flightcrewmember.flightassignment;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.flightassignment.AssigmentStatus;
import acme.entities.flightassignment.CrewsDuty;
import acme.entities.flightassignment.FlightAssignment;
import acme.entities.legs.Leg;
import acme.realms.flightcrewmember.FlightCrewMember;

@GuiService
public class FlightAssignmentsCreateService extends AbstractGuiService<FlightCrewMember, FlightAssignment> {

	@Autowired
	private CrewMemberFlightAssignmentRepository repository;


	@Override
	public void authorise() {
		int userId;
		int assignmentMemberId;
		boolean status;

		userId = super.getRequest().getPrincipal().getActiveRealm().getId();
		assignmentMemberId = super.getRequest().getData("memberId", int.class);

		status = userId == assignmentMemberId;

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void validate(final FlightAssignment assignment) {
		;
	}

	@Override
	public void load() {
		FlightAssignment assignment;
		FlightCrewMember member;
		member = (FlightCrewMember) super.getRequest().getPrincipal().getActiveRealm();

		assignment = new FlightAssignment();
		assignment.setDraftMode(true);
		assignment.setMomentLastUpdate(MomentHelper.getCurrentMoment());
		assignment.setAllocatedFlightCrewMember(member);
		super.getBuffer().addData(assignment);
	}

	@Override
	public void bind(final FlightAssignment assignment) {
		super.bindObject(assignment, "duty", "currentStatus", "remarks", "leg");
	}

	@Override
	public void perform(final FlightAssignment flightAssignment) {
		assert flightAssignment != null;
		this.repository.save(flightAssignment);
	}

	@Override
	public void unbind(final FlightAssignment assignment) {
		Dataset dataset;
		SelectChoices dutyChoice;
		SelectChoices currentStatusChoice;

		SelectChoices legChoice;
		Collection<Leg> legs;

		SelectChoices flightCrewMemberChoice;
		Collection<FlightCrewMember> flightCrewMembers;

		dutyChoice = SelectChoices.from(CrewsDuty.class, assignment.getDuty());
		currentStatusChoice = SelectChoices.from(AssigmentStatus.class, assignment.getCurrentStatus());

		legs = this.repository.findAllLegs();
		legChoice = SelectChoices.from(legs, "description", assignment.getLeg());

		flightCrewMembers = this.repository.findAllFlightCrewMembers();
		flightCrewMemberChoice = SelectChoices.from(flightCrewMembers, "id", assignment.getAllocatedFlightCrewMember());

		dataset = super.unbindObject(assignment, "duty", "momentLastUpdate", "currentStatus", "remarks", "allocatedFlightCrewMember", "leg", "draftMode");
		dataset.put("dutyChoice", dutyChoice);
		dataset.put("currentStatusChoice", currentStatusChoice);
		dataset.put("legChoice", legChoice);
		dataset.put("flightCrewMemberChoice", flightCrewMemberChoice);
		dataset.put("memberId", super.getRequest().getData("memberId", int.class));

		super.getResponse().addData(dataset);

	}

}
