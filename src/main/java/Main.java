import com.formdev.flatlaf.FlatLightLaf;
import view.DashboardView;

public class Main {
    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
        } catch (Exception ex) {
            System.err.println("Erro ao carregar FlatLaf");
        }
        new DashboardView();
    }
}
