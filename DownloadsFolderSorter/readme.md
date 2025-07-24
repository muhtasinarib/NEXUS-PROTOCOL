MythicalDownloadSorter

Project Title: MythicalDownloadSorter 
Developed By: Ariii 
Date: July 24, 2025
Purpose: A Java-based desktop automation tool to organize files in the Downloads folder by type and date, with system tray integration and scheduled execution.
Environment: Windows, Java Development Kit (JDK) 17  

Abstract
The MythicalDownloadSorter is a Java application designed to automate file organization. It scans the Downloads folder, categorizes files by their extensions (e.g., PDF, Images, Videos), and moves them into a structured hierarchy under Documents/ValhallaDownloads, with subfolders named by the file’s last modified date (e.g., Images/2025-07-24). Unopened folders are moved to a dedicated UnopenedFolders directory. The tool runs automatically on system startup and every 60 minutes via Windows Task Scheduler, and it includes a system tray icon with options to trigger sorting or exit the application. This report details the tool’s objectives, technologies, features, workflow, setup, and future enhancements, while also guiding users through the learning process from basic concepts to mastery.

Objective
The MythicalDownloadSorter aims to:

Reduce Clutter: Automatically organize the Downloads folder to keep it tidy.
Enhance Accessibility: Sort files into intuitive categories and date-based subfolders.
Automate Workflow: Run without user intervention on startup and at regular intervals.
Provide User Control: Offer a system tray interface for manual sorting and exit.
Ensure Flexibility: Support easy addition of new file types and cross-platform compatibility.
Teach Users: Demonstrate core Java concepts like file handling, data structures, and system integration in a simple, scalable way.


Technology Used

Java (JDK 17): Core programming language for logic and file operations.
Java AWT (Abstract Window Toolkit): For creating the system tray icon and menu.
Java NIO (New Input/Output): For efficient file and directory operations.
Windows Task Scheduler: To automate execution on startup and at 60-minute intervals.
Windows Command Prompt: For compiling and running the Java program.
File System: Uses Downloads and Documents directories with dynamic path resolution.
Text Editor or IDE: Any text editor (e.g., Notepad++) or IDE (e.g., IntelliJ IDEA, Eclipse) for coding.


Features

File Categorization:
Groups files by extension (e.g., .pdf → PDF, .jpg → Images).
Supports multiple categories: PDF, Images, Music, Videos, Docs, Scripts, Archives, Installers, Others.


Date-Based Organization:
Creates subfolders named by last modified date (e.g., 2025-07-24).


Folder Handling:
Moves unopened folders to ValhallaDownloads/UnopenedFolders.


Dynamic Directory Creation:
Automatically creates missing category or date folders.


No Duplicates:
Overwrites existing files during moves to avoid duplicates.


Automation:
Runs on system startup and every 60 minutes via Task Scheduler.


System Tray Integration:
Provides a tray icon with “Sort Now” and “Exit” options.


Cross-Platform Compatibility:
Uses System.getProperty("user.home") for dynamic path resolution.




Workflow
The program is structured into modular methods, each handling a specific task. Below is a detailed explanation of the logic, tailored for users.
4.1 Code Overview
The program is a single Java file, MythicalDownloadSorter.java, saved at:
C:\Users\Admin\Documents\CODES2.0\JAVA\File Sorter\MythicalDownloadSorter.java

4.2 Module/Method Breakdown
Imports

Purpose: Bring in necessary Java libraries.
Details:
java.io.File: Handles file and directory operations.
java.nio.file.*: Provides advanced file operations like moving files.
java.text.SimpleDateFormat: Formats dates for folder names.
java.util.*: Provides data structures like HashMap and ArrayList.
java.awt.*, java.awt.event.*: Enables system tray functionality.



Class and Variables

Class: MythicalDownloadSorter
A public class containing all logic.


Variables:
fileTypeMap: A HashMap mapping extensions (e.g., .pdf) to categories (e.g., PDF).
knownCategories: An ArrayList storing unique category names.
mythicalRootFolder: Set to ValhallaDownloads for the target directory.
unopenedFolderName: Set to UnopenedFolders for folder storage.
USER_HOME: Dynamically gets the user’s home directory (e.g., C:\Users\Admin).
DOWNLOADS_DIR, DOCUMENTS_DIR: File objects for source and target directories.
tray, trayIcon: AWT objects for system tray functionality.



main Method

Purpose: Entry point of the program.
Logic:
Calls initFileTypes() to set up file type mappings.
Calls setupSystemTray() to create the system tray icon.
Calls processDownloads() for initial sorting.


Note: Task Scheduler handles repeated execution.

initFileTypes Method

Purpose: Defines which file extensions belong to which categories.
Logic:
Populates fileTypeMap with mappings (e.g., .jpg → Images).
Creates a list of unique categories in knownCategories.
Adds Others and UnopenedFolders to knownCategories.



setupSystemTray Method

Purpose: Creates a system tray icon with a menu.
Logic:
Checks if the system tray is supported.
Creates a placeholder icon (requires an icon.png file in the project directory).
Adds a popup menu with “Sort Now” (triggers processDownloads) and “Exit” (closes the app).
Adds the icon to the system tray.



processDownloads Method

Purpose: Scans the Downloads folder and processes files/folders.
Logic:
Checks if Downloads exists.
Lists all items (files and folders).
For each item:
If it’s a folder, calls moveToUnopened.
If it’s a file, calls moveAndSortFile.





moveToUnopened Method

Purpose: Moves folders to ValhallaDownloads/UnopenedFolders.
Logic:
Constructs the target path.
Creates parent directories if missing.
Moves the folder using Files.move with overwrite option.



moveAndSortFile Method

Purpose: Moves files to category and date-based subfolders.
Logic:
Gets the file extension and maps it to a category (defaults to Others).
Gets the file’s last modified date.
Constructs the target path (e.g., ValhallaDownloads/Images/2025-07-24).
Creates directories if missing.
Moves the file with overwrite option.



getFileExtension Method

Purpose: Extracts the file extension.
Logic:
Finds the last . in the filename.
Returns the substring after it or an empty string if none.



getFileModifiedDate Method

Purpose: Gets the file’s last modified date in yyyy-MM-dd format.
Logic:
Uses SimpleDateFormat to format the date.



createDirectories Method

Purpose: Creates directories if they don’t exist.
Logic:
Uses mkdirs() to create the directory and its parents.




Startup and Scheduler Setup Instructions
5.1 Prerequisites

Install JDK 17:
Download from Oracle or OpenJDK.
Set up the PATH environment variable:
Right-click This PC → Properties → Advanced system settings → Environment Variables.
Add JDK’s bin folder (e.g., C:\Program Files\Java\jdk-17\bin) to PATH.




Verify Installation:
Open Command Prompt and run:java -version
javac -version


Ensure JDK 17 is listed.



5.2 Compiling and Running

Save the Code:
Save MythicalDownloadSorter.java in:C:\Users\Admin\Documents\CODES2.0\JAVA\File Sorter\




Add Icon (Optional):
Place an icon.png file in the same directory for the system tray.


Compile:
Open Command Prompt, navigate to the directory:cd C:\Users\Admin\Documents\CODES2.0\JAVA\File Sorter


Compile:javac MythicalDownloadSorter.java




Run:
Execute:java MythicalDownloadSorter


The program sorts files and adds a system tray icon.



5.3 Creating a Batch File

Create run_sorter.bat in the same directory:@echo off
cd C:\Users\Admin\Documents\CODES2.0\JAVA\File Sorter
java MythicalDownloadSorter


Double-click to run.

5.4 Task Scheduler Setup

Open Task Scheduler:
Press Win + S, type “Task Scheduler”, and open it.


Create Task:
Click “Create Task” in the Actions panel.
General tab:
Name: MythicalDownloadSorter
Check “Run whether user is logged on or not”.
Check “Run with highest privileges”.


Triggers tab:
New → “At startup” → OK.
New → “On a schedule” → Daily → Repeat every 60 minutes → OK.


Actions tab:
New → Action: Start a program.
Program: C:\Windows\System32\cmd.exe.
Arguments: /c "C:\Users\Admin\Documents\CODES2.0\JAVA\File Sorter\run_sorter.bat".


Conditions/Settings:
Uncheck “Start the task only if the computer is on AC power”.
Check “Restart if the task fails” (optional).




Save:
Enter admin credentials if prompted.
The task will run on startup and every 60 minutes.




Conclusion
The MythicalDownloadSorter is a robust Java tool that automates file organization with a mythical twist, using the ValhallaDownloads name to evoke a sense of grandeur. It leverages core Java concepts (file handling, data structures, AWT) and integrates with Windows for seamless automation. The system tray interface adds user control, while Task Scheduler ensures hands-free operation. This project serves as both a practical utility and an educational tool for learning Java.

Future Scope

GUI Interface: Use JavaFX for a graphical interface to configure categories.
Logging: Generate a sort-log.txt file to track moved files.
Duplicate Detection: Check for duplicate files before moving.
MIME Type Analysis: Use APIs to categorize unknown file types.
Cross-Platform Enhancements: Test on Linux/Mac with adjusted paths.
Cloud Integration: Sync organized files to cloud storage (e.g., Google Drive).


Guide: Learning Java from Basic Concepts to Mastery
File Handling

File Class:
Used to represent files/directories (e.g., DOWNLOADS_DIR).
Example: File file = new File("C:\\test.txt");.


Java NIO:
Files.move: Moves files efficiently.
Practice: Write a program to list all files in Downloads.



Date and String Manipulation

Dates:
SimpleDateFormat formats dates (e.g., yyyy-MM-dd).
Practice: Print today’s date in different formats.


Strings:
getFileExtension extracts extensions using lastIndexOf.
Practice: Extract the extension from document.pdf.



System Tray and AWT

AWT:
Creates the system tray icon.
Practice: Create a tray icon with a custom menu item that prints a message.
