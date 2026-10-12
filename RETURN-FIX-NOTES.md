# Return-question fix

This update fixes a blank response to questions such as `Can I returned the order`.

## Apply to an existing Phase 3 project
Copy these files from this project into your existing project's matching paths, overwriting the older files:

- `backend/src/main/java/com/example/smartsupport/chat/AgentService.java`
- `backend/src/main/java/com/example/smartsupport/chat/ChatController.java`

Keep your existing database and local environment variables. Restart the Spring Boot backend after copying. A frontend restart is not normally necessary.

## Try these messages
1. `Can I returned the order` - gives the general demo return policy and asks for an order number.
2. `Can I return order ORD-1002?` - checks the order's current status and explains the limits of the demo data.
3. `What is your return policy?` - gives the general policy directly.

The demo policy says a return request should normally be made within 30 days after delivery. The app does not submit or approve returns. Since the database stores an estimated delivery date rather than a confirmed actual delivery date, it cannot verify return eligibility for delivered orders.

## Validation note
The ZIP structure and source text were checked. A full Maven build could not be run in this environment because Maven is not installed. Run `mvn test` in the backend folder on your Windows machine before committing this change.
