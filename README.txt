1.	Project Overview 
Introduction
The CSE 360 Help System is a software project designed by our team that is intended to provide secure role-based access control and secure identity management for users that are a part of a multi-role academic system. The system will be built around the concept of multiple roles, and these roles enable individuals to function as administrators, students, or instructors. Each of these roles comes with a set of privileges that are designed to ensure the appropriate level of functionality and accessibility is provided based on the user’s role. Current features as expanded upon in the second phase include a set of help articles in which admins and instructors can create, update, categorize, and access to facilitate efficient and easy to use content management.  Moreover, phase three brings enhanced features for each role with lots of emphasis on student interaction with the system. The key improvements for the third phase include features like: an interface for students with advanced search, grouping, and message-sending capabilities for help requests; encryption for private articles, ensuring that sensitive data remains secure even if memory is accessed directly; and, special access groups, which offer restricted visibility and administrative rights for certain content. Only users with appropriate permissions can view or manage these articles, ensuring compliance with privacy standards. By the end of phase three, this project will meet comprehensive requirements in terms of user access, data security, and usability, providing a foundation for sustainable maintenance and future expansions. Finally, by phase four, the overall project has been complete, and the team will now move onto a refinement stage that will address and resolve feedback (shown at the end of the project overview), refine the requirements, architecture, and code, and enhance the application to make it the best possible product for someone else if they were to use the system. Overall, this document will outline the key components and features of the system, alongside foundational architecture and design to help showcase the functionality of the proposed system.
Login Page
Stakeholder Requirements:
•	Authentication Fields:
o	Two Text Fields – Username field and Password field
•	Login button
•	Invitation Code Field (for new users)
When a user first accesses the system, they are prompted to enter their username and password. The system will check these credentials and then route users to an appropriate home page based on their role (admin, student, or instructor). If the login happens to fail, there will be an error message displayed to the user that says “Incorrect username or password.” If the user is new, the login page will also include an input field that allows the user to enter a one-time invitation code so that they are able to make their account. Also, the first user who logs into the system will automatically become the administrator.
Admin View
Stakeholder Requirements:
•	Admin Functions
o	Invite New Users
	Generates a one-time password for users who need to reset their accounts and the admin can specify the role of user account when generating the code.
o	Reset User Accounts
	Set a one-time password for users who need to reset their accounts. This includes a specified expiration date and time.
o	Delete User Accounts
	Admins can permanently delete user accounts. This needs to be confirmed by the admin with a “Are you sure?” message before deletion.
o	List All Users
	Display a list of all user accounts which shows usernames, the roles associated with the usernames, and full names.
o	Assign/Remove Roles
	Admins can modify user roles by granting/revoking permissions for administrator, student, or instructor roles as needed.
o	Create, View, and Delete Help Articles 
	Important to note that admins cannot view or edit the bodies of encrypted articles.
o	Backup and Restore Help Articles and Special Access Groups
	Save article data to external files and restore by replacing or merging with existing articles based on unique identifiers. Special access groups can also be backed up and restored as well.
o	Grouping 
	Articles can be categorized (e.g., “Eclipse” or “H2”) by the admin, and backup can target specific groups.
o	Search Functionality
	Admins can locate articles based on keywords, phrases, or group identifiers.
o	Special Access Groups
	Manage special access groups, including group-specific access rights for other students, instructors, and admins. This is only allowed for admin roles if they are given admin rights to the special access group though. By default, admins will not have these rights.
The admin is able to log out of the system at any time by clicking on the “Logout” button which redirects to the homepage.
Student 
Student View:
Stakeholder Requirements:
o	Logout Button
o	Help Article Searching
	Perform targeted searches using either the title, author, or phrases from the abstract. 
o	Specifying Content Levels for Articles
	Filter search results based on content level (e.g., beginner, intermediate, advanced, expert) to find resources most appropriate for their knowledge level.
o	View Articles
	When presented with a summary of search results based on their search query, users can view specific article in detail view identifier number or craft a brand-new search.
o	Generic and Specific System Messaging to be Stored in System
	If a student is unable to find the needed information or feels confused about using the help system, they can send a generic message to the system’s support team, indicating a request for guidance. Students can also submit a specific help request, detailing exactly what information they were unable to locate. These messages are routed to the help system’s team, who can review and address gaps in the help resources, potentially adding new articles based on student feedback.
Instructor View:
Stakeholder Requirements:
o	Logout Button
o	Create, Update, View, and Delete Help Articles and Article Groups (including special access groups)
o	Create, Update, View, and Delete General/Special Access Groups
o	Backup and Restore Help Articles 
	Save article data to external files and restore by replacing or merging with existing articles based on unique identifiers
o	Grouping 
	Articles can be categorized (e.g., “Eclipse” or “H2”) by the instructor, and backup can target specific groups.

o	Search Functionality
	Instructors can locate articles based on keywords, phrases, or group identifiers.
o	Help Article Searching
	Perform targeted searches using either the title, author, or phrases from the abstract. 
o	Specifying Content Levels for Articles
	Filter search results based on content level (e.g., beginner, intermediate, advanced, expert) to find resources most appropriate for their knowledge level.
o	View Articles
	When presented with a summary of search results based on their search query, users can view specific article in detail view identifier number or craft a brand-new search.
o	Managing Students
	Instructors have the ability to add, view, and delete students from the help system and general groups.
o	Managing Groups
	Instructors may add, view, and delete students from the help system and general groups. If granted admin access, instructors can also add, view, and delete students from special access groups.
o	Assess Skill Levels
	Instructors may view the skill levels of all students in the database.

For both students and instructors, they will be taken to a simple home page after logging in and their only current action for now is to be able to log out of the system. If a user happens to have multiple roles, they will be prompted to select which role they will want to use for this current session. Once the role is selected, they will be taken to the corresponding home page for that role.


Account Setup Page
Stakeholder Requirements:
•	Four Text Fields
o	First Name
o	Middle Name
o	Last Name
o	Optional Preferred First Name
•	Email Address Field
•	Submit Button
After a user logs in for the first time, they are required to finish setting up their account by providing an email address and completing the accompanying name fields. Once this setup is complete, the users will be redirected to an appropriate home page based on their role.
Invitation Code and Account Creation
Stakeholder Requirements:
•	Invitation Code Field
•	Username and Password Fields
•	Create Account Button
New users who have been added to the system will be provided with a one-time invitation code provided by the administrator. Once the user enters the code, they are prompted to create a username and password. After the account creation, they are directed back to the login page to finalize their login process. For phase 1 currently, users will only have access to the home page which contains a logout button that redirects to the homepage.
Security and Privacy
Security and privacy will be essential components of the CSE 360 Help System as the system will implement: strong password management policies that ensure users create strong passwords and are required to enter them twice for verification; ability to reset passwords via the admin with expiration times to ensure password resets are temporary and secure; safeguards to prevent the reuse of one-time passwords; for special access groups, body information will be stored in an encrypted state and only users with specific access can access and view the resource to maintain security of the help article information.

Help Article Requirements
Each help article will include:
•	A unique header, grouping identifier, and any other system information that may limit who can read the article
•	A title
•	A short description
•	A set of keywords for searching processes.
•	An article body
•	A set of links to reference materials
Special Access Group Requirements
The system supports special access groups containing encrypted articles, with access limited to specific roles. Admins manage access rights, while instructors may be granted viewing and admin rights, and certain students can be given permission to view decrypted article content.
Addressing Feedback and Final Refinements
For our project in the last three phases, we only received one piece of feedback which was during phase one where we lost 5 points since “The test file containing the automated testing code was not found.” Otherwise, in the other two submissions, our group received full credit and no feedback, so this is the only main problem we need to address. While we added testing code and eventually JUnit tests onto our GitHub, our team figured that we should really focus on the testing this phase and ensure every critical function is tested. To do this, we updated our test.java file on our GitHub (link found in section “5. Code with Testing”) to test every non-GUI function across all three phases. In addition, for the GUI sections of our project, section five of this document was also updated with a series of manual testing showcases to really ensure the project functions as necessary. By including both testing in the code through methods like JUnit and testing the GUI manually, our team believes that we have thoroughly tested the submission and have adequately addressed the feedback. 
Aside from the feedback, to refine the project, our team decided to overhaul the visuals by making consistent background colors, adjusting text with fonts, sizing, and boldening, and coloring features like combo boxes and buttons. This was done to engage the user as an attractive interface with an intuitive design makes the system easier to use. Furthermore, to further increase the security of the system, the team also decided it would be beneficial to store the user’s information in the database in an encrypted manner instead of plaintext (similar to the special access group help articles) so users feel safer using the system. Finally, the last implementation for our project is allowing instructors to view the skill levels of current students so they can have a better understanding of the overall skills and make appropriate articles accordingly.
Conclusion
In conclusion, this project has evolved over four development phases to create a robust, user-friendly system that supports admins, instructors, and students in managing and accessing help articles effectively. Each phase has refined the requirements, architecture, design, and testing, addressing feedback and ensuring the system’s functionality and usability. The final phase emphasizes alignment and quality, aiming to deliver a polished product that demonstrates its value and readiness for real-world use. By addressing past issues, prioritizing usability, and enhancing system cohesion, this project stands as a testament to the team’s commitment to creating a reliable, well-documented application that meets user needs and showcases professional software development practices.

