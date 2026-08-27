# Bin Directory (Archive & Obsolete Project Support Files)

This directory (`Bin/`) contains archived, obsolete, and internal project-support artifacts that are retained for record-keeping or migration history.

### Contents:
- `.github_modernize/`: Historical automation and modernization hook scripts (`recordToolUse.ps1`, `recordToolUse.sh`).

### Important Note:
- The contents of this folder are **NOT required** for the normal build, execution, or testing of the PlantPal application.
- The standard application is started using `start.bat` at the repository root and built via Apache Maven (`pom.xml`).
