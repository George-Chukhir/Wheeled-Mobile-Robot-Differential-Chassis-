clc; clear; close all;


fileName = 'robot_log_java.csv';
try
    dataTable = readtable(fileName);
catch ME
    disp(['Chyba pri čítaní súboru: ', fileName]);
    disp(ME.message);
    return;
end

% Extrahovanie údajov do vektorov
time_log    = dataTable.Time_s; 
XT_log      = dataTable.XT_m; 
YT_log      = dataTable.YT_m; 
phi_log     = dataTable.phi_rad; 
VT_log      = dataTable.VT_mps; 
omega_T_log = dataTable.omegaT_radps; 
VL_log      = dataTable.VL_mps; 
VR_log      = dataTable.VR_mps; 

L_robot = 0.2; % Rozchod kolies



%Grafické zobrazenie
% 1. Grafy rýchlosti (V_L, V_R, V_T)
figure('Name', 'Priebehy rýchlostí (načítané z CSV)');
plot(time_log, VL_log, 'r-', 'LineWidth', 1.2, 'DisplayName', 'Ľavé koleso (V_L)');
hold on;
plot(time_log, VR_log, 'b-', 'LineWidth', 1.2, 'DisplayName', 'Pravé koleso (V_R)');
plot(time_log, VT_log, 'g-', 'LineWidth', 1.5, 'DisplayName', 'Ťažisko (V_T)');
hold off;
title('Priebeh rýchlosti pravého, ľavého kolesa a ťažiska v čase');
xlabel('Čas (s)');
ylabel('Rýchlosť (m/s)');
legend('show', 'Location','best');
grid on;
set(gca, 'FontName', 'Arial', 'FontSize', 10);

%Graf uhlovej rýchlosti
figure('Name', 'Priebeh uhlovej rýchlosti (načítané z CSV)');
plot(time_log, omega_T_log, 'm-', 'LineWidth', 1.5, 'DisplayName', '\omega_T');
title('Priebeh uhlovej rýchlosti ťažiska v čase');
xlabel('Čas (s)');
ylabel('Uhlová rýchlosť (rad/s)');
legend('show', 'Location','best');
grid on;
set(gca, 'FontName', 'Arial', 'FontSize', 10);

% 2. Grafy trajektórie (kolesá a ťažisko) 
% Výpočet súradníc kolesa zo zaznamenaných protokolov
XL_plot = XT_log - (L_robot/2) * sin(phi_log);
YL_plot = YT_log + (L_robot/2) * cos(phi_log);
XR_plot = XT_log + (L_robot/2) * sin(phi_log);
YR_plot = YT_log - (L_robot/2) * cos(phi_log);

figure('Name', 'Trajektórie (načítané z CSV)');
plot(XT_log, YT_log, 'g-', 'LineWidth', 2, 'DisplayName', 'Ťažisko');
hold on;
plot(XL_plot, YL_plot, 'r--', 'LineWidth', 1, 'DisplayName', 'Ľavé koleso');
plot(XR_plot, YR_plot, 'b--', 'LineWidth', 1, 'DisplayName', 'Pravé koleso');

if ~isempty(XT_log)
    plot(XT_log(1), YT_log(1), 'ko', 'MarkerFaceColor', 'k', 'MarkerSize', 8, 'DisplayName', 'Štart');
    plot(XT_log(end), YT_log(end), 'ks', 'MarkerFaceColor', 'r', 'MarkerSize', 8, 'DisplayName', 'Koniec');
end

hold off;
title('Trajektórie kolies a ťažiska');
xlabel('Súradnica X (m)');
ylabel('Súradnica Y (m)');
legend('show', 'Location','best');
axis equal;
grid on;
set(gca, 'FontName', 'Arial', 'FontSize', 10);
