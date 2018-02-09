package edu.wpi.first.wpilib.plugins.cpp.wizards.newproject;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import edu.wpi.first.wpilib.plugins.core.wizards.ProjectType;
import edu.wpi.first.wpilib.plugins.cpp.WPILibCPPPlugin;

public class CPPProjectType implements ProjectType {
	static ProjectType ITERATIVE = new CPPProjectType() {
		@Override public Map<String, String> getFiles(String packageName) {
			Map<String, String> files = super.getFiles(packageName);
			files.put("src/Robot.cpp", "templates/iterative/Robot.cpp");
			files.put("src/Robot.h", "templates/iterative/Robot.h");
			return files;
		}
	};
	static ProjectType TIMED = new CPPProjectType() {
		@Override public Map<String, String> getFiles(String packageName) {
			Map<String, String> files = super.getFiles(packageName);
			files.put("src/Robot.cpp", "templates/timed/Robot.cpp");
			files.put("src/Robot.h", "templates/timed/Robot.h");
			return files;
		}
	};
	static ProjectType COMMAND_BASED = new CPPProjectType() {
		@Override public String[] getFolders(String packageName) {
			String[] paths = {"src",
					"src/Commands",
					"src/Subsystems",
					"src/Triggers"};
			return paths;
		}
		@Override public Map<String, String> getFiles(String packageName) {
			Map<String, String> files = super.getFiles(packageName);
			files.put("src/Robot.cpp", "templates/commandbased/Robot.cpp");
			files.put("src/Robot.h", "templates/commandbased/Robot.h");
			files.put("src/OI.h", "templates/commandbased/OI.h");
			files.put("src/OI.cpp", "templates/commandbased/OI.cpp");
			files.put("src/RobotMap.h", "templates/commandbased/RobotMap.h");
			files.put("src/Subsystems/ExampleSubsystem.h", "templates/commandbased/Subsystems/ExampleSubsystem.h");
			files.put("src/Subsystems/ExampleSubsystem.cpp", "templates/commandbased/Subsystems/ExampleSubsystem.cpp");
			files.put("src/Commands/ExampleCommand.h", "templates/commandbased/Commands/ExampleCommand.h");
			files.put("src/Commands/ExampleCommand.cpp", "templates/commandbased/Commands/ExampleCommand.cpp");
			files.put("src/Commands/MyAutoCommand.h", "templates/commandbased/Commands/MyAutoCommand.h");
			files.put("src/Commands/MyAutoCommand.cpp", "templates/commandbased/Commands/MyAutoCommand.cpp");
			return files;
		}
	};
	static ProjectType SAMPLE = new CPPProjectType() {
		@Override public Map<String, String> getFiles(String packageName) {
			Map<String, String> files = super.getFiles(packageName);
			files.put("src/Robot.cpp", "templates/sample/Robot.cpp");
			files.put("src/Robot.h", "templates/sample/Robot.h");
			return files;
		}
	};
	@SuppressWarnings("serial")
	static Map<String, ProjectType> TYPES = new HashMap<String, ProjectType>() {{
		put(ProjectType.ITERATIVE, ITERATIVE);
		put(ProjectType.COMMAND_BASED, COMMAND_BASED);
		put(ProjectType.SAMPLE, SAMPLE);
		put(ProjectType.TIMED, TIMED);
	}};

	@Override
	public String[] getFolders(String packageName) {
		String[] paths = {"src"};
		return paths;
	}

	@Override
	public Map<String, String> getFiles(String packageName) {
		HashMap<String, String> files = new HashMap<String, String>();
		files.put("build.xml", "build.xml");
		files.put("build.properties", "build.properties");
		files.put(".cproject", ".cproject");
		return files;
	}


	@Override
	public URL getBaseURL() {
		return WPILibCPPPlugin.getDefault().getBundle().getEntry("/resources/templates/");
	}
}
