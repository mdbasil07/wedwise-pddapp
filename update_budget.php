<?php
header("Content-Type: application/json");
error_reporting(E_ALL);
ini_set('display_errors', 1);

include('db.php'); // Include database connection

$response = array();

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    if (isset($_POST['id']) && isset($_POST['total'])) {
        $id = $_POST['id'];
        $new_total = $_POST['total'];

        // Fetch existing budget details
        $stmt = $conn->prepare("SELECT total, remaining FROM budget WHERE id = ?");
        $stmt->bind_param("i", $id);
        $stmt->execute();
        $result = $stmt->get_result();
        
        if ($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            $old_total = $row['total'];
            $old_remaining = $row['remaining'];

            // Adjust remaining budget accordingly
            $new_remaining = $old_remaining + ($new_total - $old_total);

            // Update the budget
            $stmt = $conn->prepare("UPDATE budget SET total = ?, remaining = ? WHERE id = ?");
            $stmt->bind_param("iii", $new_total, $new_remaining, $id);
            
            if ($stmt->execute()) {
                $response["success"] = true;
                $response["message"] = "Budget updated successfully!";
                $response["total"] = $new_total;
                $response["remaining"] = $new_remaining;
            } else {
                $response["success"] = false;
                $response["message"] = "Failed to update budget.";
            }
        } else {
            $response["success"] = false;
            $response["message"] = "Budget ID not found.";
        }
        $stmt->close();
    } else {
        $response["success"] = false;
        $response["message"] = "Missing required fields: id, total.";
    }
} else {
    $response["success"] = false;
    $response["message"] = "Invalid request method.";
}

echo json_encode($response);
?>
