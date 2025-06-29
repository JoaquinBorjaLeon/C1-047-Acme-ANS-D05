<%@page%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>

	<acme:input-select code="flight-crew-member.flight-assignment.form.label.flightCrewDuty" path="duty" choices="${dutyChoice}"/>
	<acme:input-select code="flight-crew-member.flight-assignment.form.label.assignmentStatus" path="currentStatus" choices="${currentStatusChoice}"/>
	<acme:input-textarea code="flight-crew-member.flight-assignment.form.label.remarks" path="remarks"/>
  	<acme:input-select code="flight-crew-member.flight-assignment.form.label.leg" path="leg" choices="${legChoice}"/>
	

	<jstl:choose>
		<jstl:when test="${_command == 'show' && draftMode == false && isLegLanded}">
 		<acme:button code="flight-crew-member.flight-assignment.form.button.list-logs" action="/flight-crew-member/activity-log/list?masterId=${id}"/>
 		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update|publish') && draftMode == true}">
			<acme:submit code="flight-crew-member.flight-assignment.form.button.publish" action="/flight-crew-member/flight-assignment/publish?memberId=${memberId}"/>
			<acme:submit code="flight-crew-member.flight-assignment.form.button.update" action="/flight-crew-member/flight-assignment/update"/>
			
		</jstl:when>

		<jstl:when test="${_command == 'create'}">
			<acme:submit code="flight-crew-member.flight-assignment.form.button.create" action="/flight-crew-member/flight-assignment/create?memberId=${memberId}"/>		
		</jstl:when>
	</jstl:choose>
</acme:form>