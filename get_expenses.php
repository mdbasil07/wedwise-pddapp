<?php
header("Content-Type: application/json");
error_reporting(E_ALL);
ini_set('display_errors', 1);

include('db.php');

$response = array();

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    if (isset($_POST['budget_id'])) {
        $budget_id = $_POST['budget_id'];

        $stmt = $conn->prepare("SELECT * FROM expenses WHERE budget_id = ? ORDER BY created_at DESC");
        $stmt->bind_param("i", $budget_id);
        $stmt->execute();
        $result = $stmt->get_result();

        $expenses = array();
        while ($row = $result->fetch_assoc()) {
            $expenses[] = $row;
        }

        if (count($expenses) > 0) {
            $response["success"] = true;
            $response["expenses"] = $expenses;
        } else {
            $response["success"] = false;
            $response["message"] = "No expenses found.";
        }
        $stmt->close();
    } else {
        $response["success"] = false;
        $response["message"] = "Missing budget_id.";
    }
} else {
    $response["success"] = false;
    $response["message"] = "Invalid request method.";
}

echo json_encode($response);
?>
