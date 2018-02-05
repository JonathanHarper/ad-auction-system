import javax.swing.SwingUtilities;

public class DashboardController {

	public static void main(String[] args) {
		
		DashboardController m = new DashboardController();
		m.go();

	}

	public void go(){
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
				Data d = null;
				DashboardFrame a = new DashboardFrame(d);
				a.init();
		    }
		});
	}
}


