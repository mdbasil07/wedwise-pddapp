<?php
session_start(); // Start session
include('db.php');
header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    if (!isset($_POST['new_password'], $_POST['confirm_password'])) {
        echo json_encode(["status" => "error", "message" => "New password and confirm password are required."]);
        exit();
    }

    if (!isset($_SESSION['email'])) {
        echo json_encode(["status" => "error", "message" => "Session expired. Please verify OTP again."]);
        exit();
    }

    $email = $_SESSION['email'];
    $new_password = trim($_POST['new_password']);
    $confirm_password = trim($_POST['confirm_password']);

    // 🔹 1. Validate password length
    if (strlen($new_password) < 6) {
        echo json_encode(["status" => "error", "message" => "Password must be at least 6 characters long."]);
        exit();
    }

    // 🔹 2. Check if passwords match
    if ($new_password !== $confirm_password) {
        echo json_encode(["status" => "error", "message" => "Passwords do not match."]);
        exit();
    }

    // 🔹 3. Update password in database (WITHOUT HASHING, as per your request)
    $update_stmt = $conn->prepare("UPDATE signup SET password = ? WHERE email = ?");
    $update_stmt->bind_param("ss", $new_password, $email);

    if ($update_stmt->execute()) {
        echo json_encode(["status" => "success", "message" => "Password reset successful. You can now log in."]);
        
        session_destroy(); // Clear session after response
    } else {
        echo json_encode(["status" => "error", "message" => "Error resetting password. Please try again."]);
    }

    $update_stmt->close();
    $conn->close();
}
?>
