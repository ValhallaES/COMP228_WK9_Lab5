module Lab5 {
	    requires javafx.controls;
		requires javafx.base;
		requires java.sql;
	    exports application;
	    opens application to javafx.graphics;
	}
