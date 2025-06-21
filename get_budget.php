<?php
header("Content-Type: application/json");
error_reporting(E_ALL);
ini_set('display_errors', 1);

include('db.php');

$response = array();

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    if (isset($_POST['id'])) {
        $id = $_POST['id'];

        $stmt = $conn->prepare("SELECT id, total, remaining, created_at FROM budget WHERE id = ?");
        $stmt->bind_param("i", $id);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $budget = $result->fetch_assoc();
            $response["success"] = true;
            $response["budget"] = $budget;
        } else {
            $response["success"] = false;
            $response["message"] = "No budget found with this ID.";
        }
        $stmt->close();
    } else {
        $response["success"] = false;
        $response["message"] = "Missing ID parameter.";
    }
} else {
    $response["success"] = false;
    $response["message"] = "Invalid request method.";
}

echo json_encode($response);
?>
