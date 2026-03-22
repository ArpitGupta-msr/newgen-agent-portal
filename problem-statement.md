# NewGen Insurance Agent Application

### Project Description

NewGen Insurance Agent application is a web based and Mobile application which provides a user-friendly platform for agent/Sales personnel. It uses microservice architecture in which multiple microservices work together to give seamless experience to agents to perform day to day activities like business management, consent management, customer portfolio management, etc. The backend of the application is built using Spring Framework whereas the frontend is built using React.

### Major Modules
- Agent Registration
- Agent Login

### Architecture

This application uses Microservice Architecture to make it a robust, scalable and easy to maintain application. Each microservice is responsible for individual task and different microservices communicate with each other using REST APIs.

### Technology Stack
- **Backend:** Spring Boot, Spring Data, Spring REST, Spring Cloud Consul
- **Frontend:** React
- **Database:** MySQL

### Non-Functional Requirements
- **Performance:** The application should be fast and responsive, with quick load times and minimal lag or delay.
- **Scalability:** The application should be able to manage large amounts of traffic and scale as needed to accommodate growth.
- **Security:** The application should be secure and protect user data from unauthorized access, with measures such as encryption and secure authentication.
- **Reliability:** The application should be reliable and available, with minimal downtime or outages.
- **Usability:** The application should be easy to use and intuitive, with a clear and consistent user interface.
- **Maintainability:** The application should be easy to maintain and update, with clear and well-organized code that is easy to understand and modify.

### Implementation Guidelines
- Utilize Lombok for model class creation and logging.
- Implement logic using Lambdas and Streams.
- Organize code into logical layers (controller, service, DTO, entity, etc.).
- Use meaningful package names reflecting the domain.
- Employ Spring dependency injection for effective component management.

### General Guidelines

#### Error Handling
- Handle cases where the user attempts to access without authorization.
- Implement a custom exception to throw user-defined messages.
- Centralized exception handling mechanism should be used to capture exceptions and translate them into HTTP response.
- Implement proper error handling mechanisms to catch any issues and log all the service exceptions using LoggingAspect.
- Handle success and error responses appropriately with proper messages.

#### DTOs and Mapping
- Data Transfer Objects (DTOs) should be used for API request and response.
- Entities should not be exposed directly to APIs.
- Use ModelMapper to convert from entities to dto and vice versa.

#### Database Interaction
- Spring Data Repository should be used for database operation.
- Add appropriate properties specific to the application like database properties and table generation strategies.

#### API Design
- All the api should be mapped with the base URI '/newgen'.
- Set server port number of your choice.
- Use appropriate HTTP methods and status code for all rest end points.
- Use Swagger for generating the API documentation.

#### Microservice Communication
- Circuit Breaker pattern should be implemented for all the critical services with a timeout of 3 seconds and error threshold of 50%. The circuit breaker should open after 10 consecutive failures and remain open for 60 seconds. Upon circuit breaker opening, a fallback mechanism should return a generic message to the client.
- Fallback behaviors should be defined for all critical operations.
- Create two instances for any Microservice and implement load balancing.

### Validation
- Bean validation should be used for all inputs throughout the process.
- Custom Validators should be used for complex validation.
- All request parameters must be validated for null or empty values. For any such invalid values, 'Please provide a valid {attribute name}' should be the error message.
- Any date and time value should not start with zero.

### Testing
- Write JUnit test cases for all service methods using Mockito.
- 80% code coverage should be achieved.

### Code Quality

To ensure adherence to coding standards, the project should be analyzed using SonarQube. The following minimum acceptable values must be met:

| SonarQube Metrics  | Minimum Acceptable Value |
|--------------------|--------------------------|
| Security           | A                        |
| Reliability        | A                        |
| Issues             | <= 5                     |
| Coverage           | >= 80%                   |
| Duplications       | <= 3%                    |
| Security Hotspots  | A                        |

### Postman
Once done with implementing the requirements, use Postman or any other posting tools to test whether the REST endpoints are working fine.

### US 01: Agent Login and Sign UP

**Story:** First Time Sign Up — As an agent, I want to be able to select my user role so that I can access the appropriate features.

#### Acceptance Criteria

**Welcome Screen:**
- The agent should see a welcome message at the top of the screen.
- Below the welcome message, there should be a section for role selection.

**Role Selection:**

The following roles should be displayed:
- Agent
- DO
- CLIA
- LICA

The role 'Agent' should be pre-selected for this use case.

**Get Started Button:**
- After selecting the user role, the 'Get Started' button should become active.
- Clicking the 'Get Started' button should take the agent to the next screen in the sign-up process.
- If no role is selected, the default should remain 'Agent'.

### US 02: Agency Code Entry & Consent Screen

**Story:** As an agent, I want to enter my agency code so that I can proceed with the verification process. If I enter an incorrect agency code, I should receive a message saying, "Incorrect agency code, please enter valid code".

#### Acceptance Criteria

**Agency Code Input:**
- The agent should see an input field labeled "Enter Agency Code".
- The agent should be able to enter their agency code in this field.
- The input field should validate the agency code format (e.g., length, allowed characters).

**Error Handling for Incorrect Agency Code:**
- If the agent enters an incorrect agency code, an error message should be displayed below the input field.
- The error message should read: "Incorrect agency code, please enter a valid code."
- The input field should be cleared or highlighted to indicate the error.

**Consent Checkbox:**
- Below the agency code input field, there should be a checkbox with the label: "I agree to the terms and conditions and acknowledge the privacy policy."
- The terms and conditions and privacy policy should be linked to their respective documents (if applicable).
- The agent must click on the checkbox to provide consent.

**Call to Action (CTA) Button:**
- The 'Request for OTP' button should be disabled by default.
- The button should only become enabled after the agent has entered a valid agency code and checked the consent checkbox.
- Clicking the 'Request for OTP' should generate an OTP. (You can use the following API to generate and validate the OTP: http://vjeemys-48:7000/otp)
- The consent checkbox should be mandatory to proceed.

### US 03: OTP Verification

**Story:** As an agent, after entering my agency code and providing consent, I want to receive an OTP so that I can verify my identity.

#### Acceptance Criteria

**Requesting OTP:**
- After the agent enters their agency code and provides consent, they should click the 'Request for OTP' button.
- Upon clicking the button, an OTP (One-Time Password) will be generated. (You can use the following API to generate and validate the OTP: http://vjeemys-48:7000/otp)

**Resending OTP:**
- The agent should have the option to resend the OTP up to 2 times.
- There should be a 5 min interval between each OTP resend attempt.
- A 'Resend OTP' button should be available for the agent to click if they need to resend the OTP.

**Handling Incorrect OTP:**
- If the agent enters an incorrect OTP, an error message should be displayed.
- The error message should read: "Incorrect OTP, please try again".
- The input field for the OTP should be cleared or highlighted to indicate the error.

**Successful OTP Verification:**
- Upon successful entry and verification of the OTP, the agent should be shown a personalized welcome message.
- The welcome message should read: "Hi {Name}. Your verification has been done successfully."
- A 'Next' button should be available for the agent to proceed to the next step in the process.
- If the OTP verification fails, an appropriate failure message should be displayed, and the agent should be prompted to try again.

### US 04: Credential Set-Up: Password

**Story:** As an agent, if I select a password as my credential, I want to enter and confirm my password for secure login.

#### Acceptance Criteria

**Password Requirements:**
- The password must be at least 8 characters long.
- It must include at least:
  - One uppercase letter (A-Z)
  - One lowercase letter (a-z)
  - One symbol (e.g., @, #, $, etc.)
  - One number (0-9)
  - The password should not contain any spaces.

**Validation of Password Requirements:**
- If the password does not meet the above criteria, an error message should be displayed.
- The error message should read: "Password does not meet the recommended criteria."

**Password Confirmation:**
- The agent must enter the password twice: once in the "Password" field and once in the "Confirm Password" field.
- Hash the user password using a secure hashing algorithm (e.g., bcrypt) for storage.
- If the entered password does not match the confirmed password, an error message should be displayed.
- The error message should read: "Passwords do not match".

**Successful Sign-Up:**
- Upon successful entry and confirmation of the password, the agent should receive a success message.
- The success message should confirm that the password has been set up successfully and the agent can proceed with the login process.

### US 05: Credential Set-Up: MPIN

**Story:** As an agent, if I select MPIN as my credential, I want to enter and confirm my MPIN so that I can use it for secure login.

#### Acceptance Criteria

**MPIN Requirements:**
- The MPIN should be exactly 4 digits long.
- Only numerical digits (0-9) should be allowed.

**Validation of MPIN:**
- The agent must enter the MPIN twice: once in the "MPIN" field and once in the "Confirm MPIN" field.
- If the entered MPIN does not match the confirmed MPIN, an error message should be displayed.
- The error message should read: "MPINs do not match".

**Successful Sign-Up:**
- Upon successful entry and confirmation of the MPIN, the agent should receive a success message.
- The success message should confirm that the MPIN has been set up successfully and the agent can proceed with the login process.

### US 06: Agent Login

**Story:** As an agent, I want to choose my preferred login credentials (MPIN, password) so that I can securely access my account in a way that suits me.

#### Acceptance Criteria

**Login with MPIN:**
- The agent should be able to select MPIN as their login credential.
- The agent should enter their agency code and MPIN to authenticate and access their account.
- If the entered MPIN is incorrect, an error message should be displayed.
- The error message should read: "Incorrect MPIN. Please try again."
- The error message should remain until the correct MPIN is entered.

**Login with Password:**
- The agent should be able to select a password as their login credential.
- The agent should enter their agency code and password to authenticate and access their account.
- If the entered password is incorrect, an error message should be displayed.
- The error message should read: "Incorrect password. Please try again".
- The error message should remain until the correct password is entered.

**Successful Login:**
- Upon successful login, a welcome message should be displayed on the home screen.
- This message can include the user's name or other relevant details to make the greeting more engaging and personalized.
