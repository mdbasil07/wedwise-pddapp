<?php
header("Content-Type: application/json");
error_reporting(E_ALL);
ini_set('display_errors', 1);

include('db.php'); // Include database connection

$response = array();

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    if (isset($_POST['total'])) {
        $total = $_POST['total'];
        $remaining = $total; // Initially, remaining = total

        $stmt = $conn->prepare("INSERT INTO budget (total, remaining, created_at) VALUES (?, ?, NOW())");
        $stmt->bind_param("ii", $total, $remaining);

        if ($stmt->execute()) {
            $response["success"] = true;
            $response["message"] = "Budget added successfully!";
            $response["id"] = $stmt->insert_id; // Return the inserted budget ID
        } else {
            $response["success"] = false;
            $response["message"] = "Failed to add budget.";
        }
        $stmt->close();
    } else {
        $response["success"] = false;
        $response["message"] = "Missing required field: total.";
    }
} else {
    $response["success"] = false;
    $response["message"] = "Invalid request method.";
}

echo json_encode($response);
?>
