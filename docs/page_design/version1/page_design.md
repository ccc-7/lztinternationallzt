# page design introdoction

This file is to explain the composition, design logic, and implemented functions of each interface.

# Interface 1: Login Portal
This is the entry point for the entire system. Its design is clean and straightforward, with core functions centered on authentication and role-based routing.

    1.1 Composition:
    Header Area: The main title “Teaching Assistant Portal” and subtitle “BUPT International School” clearly identify the system’s affiliation.
    Login Form Area:
    Role Selector: Three side-by-side buttons — “TA Login”, “MO Login”, and “Admin Login” — allow users to select their identity before logging in. The currently selected option (“TA Login”) is highlighted in magenta.
    Input Fields: Standard text fields for “Username” and “Password”.
    Action Buttons: A prominent magenta “Sign in” primary button, and a secondary “Create TA account” button for new user registration.
    Demo Accounts Section: At the bottom, a “Demo accounts” area lists test credentials for all three roles, enabling quick exploration of different permission levels.
    Footer: A small line of text — “School recruitment and allocation platform” — summarizes the platform’s purpose.

    1.2 Design Logic:
    Clear Role Segmentation: The top-level role selector immediately directs user flows to appropriate backend modules, preventing permission conflicts.
    Visual Guidance: High-contrast magenta is used as the accent color to highlight key interactive elements (selected login type and “Sign in” button), guiding user actions.
    Transparency & Accessibility: Providing demo accounts lowers the barrier to entry for first-time users, facilitating understanding and testing.

    1.3 Implemented Functions:
    User authentication.
    Role-based redirection to corresponding dashboards (TA, MO, Admin).
    Registration portal for new TA applicants.


# Interfaces 2: Teaching Assistant (TA) Dashboard
These two images represent consecutive sections of the same page — the personalized dashboard displayed after a TA successfully logs in. It serves as an information hub designed to help applicants efficiently manage their job-seeking process.

    2.1 Composition:
    Global Navigation Bar (Left Sidebar): Contains links to major functional modules: “Dashboard”, “Available Positions”, “My Applications”, and “Log out”.
    Top Status Bar: Displays the logged-in user’s name (“Seele”) and a “Log out” button.
    Main Content Area – Dashboard Overview:
    Greeting: “Hi, Seele!”
    Application Timeline: Core real-time feed showing key updates such as “Application summary updated”.
    Smart Recommendations (“Recommended positions”): System proactively suggests matching job openings based on the TA’s skills (e.g., Java, Data Structure) and experience, including a match score (67%).
    Profile Readiness: Prompts users to update their profile information to improve matching success rates.
    Data Cards: Four cards visually summarize key metrics: “Active applications” , “Matched positions” , “Next actions” , and “Profile overview”.
    Personal Profile Section (“My profile”): (Primarily visible in Image 3) Displays detailed user information: username, full name, email, academic year, major, and skill tags.
    Quick Tools Panel (Right Sidebar):
    Position Search: Includes a search box and buttons for “Search my matches” and “Search all positions”.
    Recently Used / Quick Access: Provides shortcuts to frequently used features like viewing application status or browsing open positions.

    2.2 Design Logic:
    User-Centric Design: The entire interface revolves around the TA’s application journey — from discovering opportunities to tracking progress — with all functions readily accessible.
    Information Hierarchy & Visualization: Critical notifications are placed in the timeline; key statistics are emphasized via data cards, allowing users to grasp the big picture at a glance.
    Proactivity & Intelligence: The “Recommended positions” module demonstrates the system’s intelligent matching capability, shifting from passive searching to active recommendation — enhancing both user experience and recruitment efficiency.

    2.3 Implemented Functions:
    Display personal application status and progress.
    Receive system notifications and position recommendations.
    View and edit personal resume/profile.
    Search and browse all available TA positions.
    Quick access to commonly used tools.


# Interface 3: Module Organiser (MO) Workbench
This interface is tailored for course instructors (Module Organisers) to post and manage TA recruitment needs for their own courses.

    3.1Composition:
    Global Navigation Bar (Left Sidebar): Includes Overview — current location,Post Position,Manage Applications and Log Out.
    Top Status Bar: Shows logged-in user “Dr.Wang” and Log Out button.
    Main Content Area – MO Workbench:
    Title & Description: “MO Workbench, with brief functionality description: “Post positions, screen applicants, quickly complete this semester’s TA recruitment.”
    Overview Data Cards: Four cards clearly display core recruitment metrics(the following data are used as examples):
    Open Positions: 1
    Pending Applications: 1
    Hired: 0
    Next Step: “Review”, with prompt: “Suggest processing pending applications ASAP.”

    3.2 Design Logic:
    Task-Oriented: The interface is highly focused — its sole purpose is to streamline the recruitment workflow: Post → Screen → Hire.
    Efficiency First: Numeric cards enable MOs to instantly assess workload (how many applications need review), while the “Next Step” guidance drives workflow progression.
    Localization: Fully Chinese-language interface aligns with domestic university faculty usage habits.

    3.3 Implemented Functions:
    View overview of recruitment activities for assigned courses.
    Post new TA job openings.
    Manage and screen received TA applications.
    Track recruitment progress (pending, hired, etc.).


# Interface 4: Administrator (Admin) Control Panel
This interface is designed for system administrators, providing a global perspective to monitor overall platform performance.

    4.1 Composition:
    Global Navigation Bar (Left Sidebar): Includes Workload Overview — current location and Log Out.
    Top Status Bar: Shows logged-in user “System Admin” and Log Out button.
    Main Content Area – Admin Management Panel:
    Panel Title & Description: “Admin Admin Management Panel, function: “View distribution of all TA applications and workload conditions.”
    Data Table: A simple table listing TA User ID and corresponding Number of Positions Applied For. Example: User U001 has applied for 1 position.

    4.2 Design Logic:
    Macro Monitoring: Unlike the personalized views for TAs and MOs, the Admin view is global and data-driven. It focuses not on individual application details but on aggregate trends and distributions.
    Minimalism: Extremely clean interface with only one core data table — aligned with admin needs for quickly obtaining key summarized information.
    Scalability: Current table is illustrative; actual admin panels may include additional charts and dimensions — e.g., application per course, average applications per TA, etc.

    4.3 Implemented Functions
    Monitor activity patterns of all TA users across the platform.
    Statistically analyze application distribution for resource allocation or system optimization.
    Ensure smooth operation of the entire recruitment ecosystem.
