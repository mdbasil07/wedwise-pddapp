<?php
header("Content-Type: application/json");
include('db.php');

$response = array();

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    if (isset($_POST['total'])) {
        $total = $_POST['total'];

        // Insert or update the menu budget
        $stmt = $conn->prepare("INSERT INTO menu_budget (total, remaining) VALUES (?, ?) 
                                ON DUPLICATE KEY UPDATE total = VALUES(total), remaining = VALUES(remaining)");
        $stmt->bind_param("ii", $total, $total);

        if ($stmt->execute()) {
            $response["success"] = true;
            $response["message"] = "Menu budget set successfully!";
        } else {
            $response["success"] = false;
            $response["message"] = "Failed to set menu budget.";
        }
        $stmt->close();
    } else {
        $response["success"] = false;
        $response["message"] = "Missing total.";
    }
} else {
    $response["success"] = false;
    $response["message"] = "Invalid request method.";
}

echo json_encode($response);
?>
