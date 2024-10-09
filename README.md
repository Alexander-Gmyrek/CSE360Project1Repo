Introduction
The CSE 360 Help System is a software project designed by our team that is intended to provide secure role-based access control and secure identity management for users 
that are a part of a multi-role academic system. The system will be built around the concept of multiple roles, and these roles enable individuals to function as 
administrators, students, or instructors. Each of these roles comes with a set of privileges that are designed to ensure the appropriate level of functionality and 
accessibility is provided based on the user’s role. Overall, this document will outline the key components and features of the system, alongside a foundational 
architecture and design to help showcase the functionality of the proposed system.


Login Page:

  Stakeholder Requirements:
    i)Authentication Fields:
      - Two Text Fields – Username field and Password field
      - Login button
      - Invitation Code Field (for new users)

   ii) When a user first accesses the system, they are prompted to enter their username and password. The system will check these 
    credentials and then route users to an appropriate home page based on their role (admin, student, or instructor). If the login 
    happens to fail, there will be an error message displayed to the user that says “Incorrect username or password.” If the user 
    is new, the login page will also include an input field that allows the user to enter a one-time invitation code so that they 
    are able to make their account. Also, the first user who logs into the system will automatically become the administrator.

Admin View:
  Stakeholder Requirements:
   i) Admin Functions
      - Invite New Users
      - Generates a one-time password for users who need to reset their accounts and the admin can specify the role of user account when generating the code.
      - Reset User Accounts
      - Set a one-time password for users who need to reset their accounts. This includes a specified expiration date and time.
      - Delete User Accounts
      - Admins can permanently delete user accounts. This needs to be confirmed by the admin with a “Are you sure?” message before deletion.
      - List All Users
      - Display a list of all user accounts which shows usernames, the roles associated with the usernames, and full names.
      - Assign/Remove Roles

  ii) Admins can modify user roles by granting/revoking permissions for administrator, student, or instructor roles as needed.
  The admin is able to log out of the system at any time by clicking on the “Logout” button which redirects to the homepage.


Student and Instructor Views:
  Stakeholder Requirements:
      - Single Button Function
      - Logout Button
  i) For both students and instructors, they will be taken to a simple home page after logging in and their only current action for 
  now is to be able to log out of the system. If a user happens to have multiple roles, they will be prompted to select which role they 
  will want to use for this current session. Once the role is selected, they will be taken to the corresponding home page for that role.

Account Setup Page:
  Stakeholder Requirements:
    - Four Text Fields
    - First Name
    - Middle Name
    - Last Name
    - Optional Preferred First Name
    - Email Address Field
    - Submit Button

  i) After a user logs in for the first time, they are required to finish setting up their account by providing an email address and 
    completing the accompanying name fields. Once this setup is complete, the users will be redirected to an appropriate home page based
    on their role.


Invitation Code and Account Creation:
  Stakeholder Requirements:
    - Invitation Code Field
    - Username and Password Fields
    - Create Account Button
  i) New users who have been added to the system will be provided with a one-time invitation code provided by the administrator. Once the user 
    enters the code, they are prompted to create a username and password. After the account creation, they are directed back to the login page
    to finalize their login process. For phase 1 currently, users will only have access to the home page which contains a logout button that redirects 
    to the homepage.

Security and Privacy:
Security and privacy will be essential components of the CSE 360 Help System as the system will implement: strong password management policies 
that ensure users create strong passwords and are required to enter them twice for verification; ability to reset passwords via the admin with 
expiration times to ensure password resets are temporary and secure; safeguards to prevent the reuse of one-time passwords; storage of personal 
information in an encrypted manner. 


Conclusion
This phase of the project will be focused only on the implementation of core identity management features and this strong foundation for user 
authentication and role-based access control will be expanded upon in future phases as especially with the student and instructor roles, their 
functionalities remain simple for now.
