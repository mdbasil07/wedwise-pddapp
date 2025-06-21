<?php
header("Content-Type: application/json");
include('db.php');

$response = array();

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    if (isset($_POST['menu_budget_id'])) {
        $menu_budget_id = $_POST['menu_budget_id'];

        $stmt = $conn->prepare("SELECT * FROM menu_items WHERE menu_budget_id = ?");
        $stmt->bind_param("i", $menu_budget_id);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            $items = array();
            while ($row = $result->fetch_assoc()) {
                $items[] = $row;
            }
            $response["success"] = true;
            $response["items"] = $items;
        } else {
            $response["success"] = false;
            $response["message"] = "No menu items found.";
        }
        $stmt->close();
    } else {
        $response["success"] = false;
        $response["message"] = "Missing menu_budget_id.";
    }
} else {
    $response["success"] = false;
    $response["message"] = "Invalid request method.";
}

echo json_encode($response);
?>
