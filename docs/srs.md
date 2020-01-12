# Travlyn - Software Requirements Specification

# 1 Introduction

## 1.1 Purpose

This document serves the purpose of defining and specifying all requirements for Travlyn. In order to agree on a general consent between developers, this document lists all feature that will definitely be implemented as well as optional features.

## 1.2 Scope

Travlyn is an intelligent travel and city guide that provides interest-based trips in cities and countries. Depending on available time, interests, budget and many other parameters, _Travlyn_ creates personalized routes with additional information about the locations themselves and the sights.

Travlyn's creation of personalized routes is based on a machine learning model and can be enhanced with additional features such as automatically generated audio guides.

# 2 Overall Description

## 2.1 Project Vision

Travlyn provides an intelligent travel guide that helps you generate trips by taking into account your interests and preferences. This travel guide is highly personalized and can be used out of the box.

Traveling with your friends or alone - key is that _Travlyn_ gives you the best travel experience by providing additional background information for the selected points of interests.

## 2.2 General Use Case Diagram

Following UCD will provide a basic overview to the user characteristics, features, constraints, associations and dependencies within the project scope. Furthermore, non-functional requirements are put outside the system boundaries.

[Here](ucd/UCD.pdf) can you find our Use Case Diagram.

# 3 Specific Requirements

## 3.1 System Architecture

The architecture of _Travlyn_ is divided in backend and frontend: the backend is a RESTful API written in Java, that provides the frontend with information and data meaning that the business logic is placed in the API. The API is specified the Swagger Open API Standard. Furthermore, deployment is easy as the server can be executed with Docker.

The frontend is an Android application, that is as well written in Java. The connection to the _Travlyn_ API is carried out by HTTP Requests.

## 3.2 Functionality

### 3.2.1 Trips

One core feature of _Travlyn_ is to generate trips by providing different parameters like city, time of stay, interests etc. After the trip was created, it is stored in a database in order to access it again. The user can access on all his stored trips as well as all public trips or - as said - generate a new one.

The trips is created by selecting relevant suitable point of interests, collecting information about the points, putting them in order and finally storing them into database.

In the process of taking the trip, additional information about the users current location or locations nearby (like e.g. special buildings, sights etc.) is displayed to the user directly. An optional extension would be that this information is read out to the user by text-to-speech mechanisms.

### 3.2.2 User Management

A user can register in the system. When signing in the app, the user can access all his saved trips. The user can besides edit his personal information.

A registered user also can modify his created trips. Furthermore he can share saved trips with other users in order to go on this trip.
