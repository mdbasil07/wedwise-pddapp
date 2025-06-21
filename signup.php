<?php
session_start(); // Start session
header("Content-Type: application/json");
include __DIR__ . "/db.php"; // Ensure database connection

$response = array();

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Retrieve form data
    $name = $_POST["name"] ?? "";
    $email = $_POST["email"] ?? "";
    $password = $_POST["password"] ?? "";
    $confirm_password = $_POST["confirm_password"] ?? "";

    if (empty($name) || empty($email) || empty($password) || empty($confirm_password)) {
        $response["status"] = "error";
        $response["message"] = "All fields are required.";
    } elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $response["status"] = "error";
        $response["message"] = "Invalid email format.";
    } elseif ($password !== $confirm_password) {
        $response["status"] = "error";
        $response["message"] = "Passwords do not match.";
    } else {
        // Insert into MySQL table (no password hashing)
        $stmt = $conn->prepare("INSERT INTO signup (name, email, password) VALUES (?, ?, ?)");
        $stmt->bind_param("sss", $name, $email, $password);

        if ($stmt->execute()) {
            // ✅ Fetch the MySQL-generated AUTO_INCREMENT ID
            $user_id = $conn->insert_id; // Get the last inserted ID

            // Store user info in session
            $_SESSION["user_id"] = $user_id;
            $_SESSION["name"] = $name;
            $_SESSION["email"] = $email;

            $response["status"] = "success";
            $response["message"] = "Signup successful!";
            $response["user"] = array(
                "id" => strval($user_id), // Convert ID to string for consistency
                "name" => $name,
                "email" => $email
            );
        } else {
            $response["status"] = "error";
            $response["message"] = "Signup failed. Please try again.";
        }
        $stmt->close();
    }
} else {
    $response["status"] = "error";
    $response["message"] = "Invalid request method.";
}

echo json_encode($response, JSON_PRETTY_PRINT);
?>
