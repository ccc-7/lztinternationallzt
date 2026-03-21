Prototype
This document presents a Low-fidelity Prototype of the system. It focuses on core user interaction flows and role-based access control, aiming to validate software requirements and gather early feedback through visualization.
TA_Portal
Figure 1: This is the core entry point of the system. To enhance user experience and minimize input errors, the interface features role-switching tabs (TA/MO/Admin Login), ensuring users with different permissions access the correct subsystem. By clicking "Create TA account," users initiate the registration process. The system requires users to not only set basic security credentials but also define their specific identity (TA, MO, or Admin). This step is critical as it dictates the data categorization and access control logic within the back-end storage (plain text files).
TA_Dashboard
Figure 2: The TA Dashboard serves as the central hub for applicants. Using a modern card-based layout, it displays active application status, the number of matched positions, and pending actions. It also integrates a profile overview (showing major, year, and skill tags) and AI-powered recommendations to help users quickly assess their eligibility for open roles.
Job_Hall 
Figure 3: The Job Hall interface lists all available TA positions. Each job card details the course code, organizing professor, expected workload, year requirements, and specific skill tags. Notably, an "AI Match Score" is provided for each job, calculated based on the alignment between the user's profile and the job requirements to guide them toward the most suitable roles. 
TA_Application
Figure 4: This interface allows TAs to monitor the progress of all their submitted applications in real-time. The table clearly presents the Application ID, Job ID, current status (e.g., "PENDING"), submission time, and remarks. This transparent feedback mechanism aligns with Agile design principles to enhance user trust.
MO_Dashboard
Figure 5: This is the central control panel for the Module Organizer (MO). The interface provides a real-time data overview through four key metric cards: number of open positions, pending applications, hired TAs, and a system-suggested "Next Step." This design allows MOs to quickly grasp current recruitment progress and prioritize backlogged applications, reflecting the focus on efficiency in Agile development.
Job_Post
Figure 6: This interface is used by MOs to post new recruitment requirements. The MO enters the job title, course code, instructor name, total workload hours, and the target applicant's year range (minimum/maximum year). A detailed "Skill Requirements" field is provided at the bottom; this structured data serves as the foundation for the system's AI matching and filtering processes.
Job_Manage
Figure 7：In this interface, MOs can centrally process all received applications. The list displays the application ID, user ID, corresponding job ID, and current review status. In the "Actions" column, MOs can directly click "Hire" or "Reject" buttons.
Admin_center
Figure 8: This is the exclusive monitoring panel for the Administrator, designed to provide a macro perspective of the TA recruitment across the school. The interface displays a clear list of TA User IDs and their corresponding "Number of Applied Positions". This design allows the Administrator to identify uneven application distributions in real-time, assisting in workload balancing to ensure rational resource allocation. This is one of the core features for maintaining fairness within the system.
