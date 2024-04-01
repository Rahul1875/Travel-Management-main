create database travel;
show databases;
use travel;


	CREATE TABLE IF NOT EXISTS travel_packages (
		id INT AUTO_INCREMENT PRIMARY KEY,
		name VARCHAR(255) NOT NULL,
		passenger_capacity INT NOT NULL,
		itinerary TEXT NOT NULL,
		description TEXT NOT NULL,
		cost DOUBLE NOT NULL
	);

select * from travel_packages;

	-- Table for Locations (Combining Destinations and Activities)
	CREATE TABLE IF NOT EXISTS locations (
		location_id INT AUTO_INCREMENT PRIMARY KEY,
		destination_name VARCHAR(255),
		activity_name VARCHAR(255),
		travel_package_id INT,
		activity_cost DOUBLE NOT NULL,
		activity_cap INT NOT NULL,
		FOREIGN KEY (travel_package_id) REFERENCES travel_packages(id),
		UNIQUE (destination_name, activity_name)
	);

select * from locations;




	-- Table for Passengers
	CREATE TABLE IF NOT EXISTS passengers (
		passenger_id INT AUTO_INCREMENT PRIMARY KEY,
		name VARCHAR(255) NOT NULL,
		passenger_number VARCHAR(20) NOT NULL,
		passenger_type ENUM('standard', 'gold', 'premium') NOT NULL,
		balance DOUBLE,
		UNIQUE (passenger_number)
	);

	-- Table for Passengers' Activities
	CREATE TABLE IF NOT EXISTS passenger_activities (
		activity_id INT AUTO_INCREMENT PRIMARY KEY,
		location_id INT,
		passenger_id INT,
		FOREIGN KEY (location_id) REFERENCES locations(location_id),
		FOREIGN KEY (passenger_id) REFERENCES passengers(passenger_id),
		UNIQUE (location_id, passenger_id)
	);

select *from passenger_activities;
select* from passengers;
 