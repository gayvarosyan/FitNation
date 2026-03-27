<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FitNation</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Segoe UI', sans-serif;
        }
        body {
            background-color: #0f0f0f;
            color: #ffffff;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .card {
            background-color: #1a1a1a;
            border-radius: 16px;
            padding: 40px;
            width: 100%;
            max-width: 420px;
            box-shadow: 0 8px 32px rgba(0,0,0,0.4);
        }
        .logo {
            text-align: center;
            margin-bottom: 24px;
        }
        .logo-icon {
            background-color: #1e3a2f;
            border-radius: 12px;
            width: 60px;
            height: 60px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 12px;
            font-size: 28px;
        }
        .logo h1 {
            font-size: 24px;
            font-weight: 700;
        }
        .logo p {
            font-size: 13px;
            color: #888;
            margin-top: 4px;
        }
        .form-group {
            margin-bottom: 16px;
        }
        .form-group label {
            display: block;
            font-size: 13px;
            color: #aaa;
            margin-bottom: 6px;
        }
        .form-group input,
        .form-group textarea {
            width: 100%;
            padding: 12px 16px;
            background-color: #2a2a2a;
            border: 1px solid #333;
            border-radius: 8px;
            color: #ffffff;
            font-size: 14px;
            outline: none;
            transition: border-color 0.2s;
        }
        .form-group input:focus,
        .form-group textarea:focus {
            border-color: #4ade80;
        }
        .form-group textarea {
            resize: vertical;
            min-height: 80px;
        }
        .btn {
            width: 100%;
            padding: 13px;
            border: none;
            border-radius: 8px;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            transition: opacity 0.2s;
        }
        .btn:hover { opacity: 0.9; }
        .btn-primary {
            background-color: #4ade80;
            color: #000000;
        }
        .btn-secondary {
            background-color: #2a2a2a;
            color: #ffffff;
            border: 1px solid #444;
        }
        .link-row {
            text-align: center;
            margin-top: 16px;
            font-size: 13px;
            color: #888;
        }
        .link-row a {
            color: #4ade80;
            text-decoration: none;
        }
        .link-row a:hover { text-decoration: underline; }
        .forgot {
            text-align: right;
            margin-bottom: 12px;
        }
        .forgot a {
            font-size: 13px;
            color: #4ade80;
            text-decoration: none;
        }
        .role-toggle {
            display: flex;
            background-color: #2a2a2a;
            border-radius: 8px;
            padding: 4px;
            margin-bottom: 20px;
        }
        .role-toggle button {
            flex: 1;
            padding: 8px;
            border: none;
            border-radius: 6px;
            background: transparent;
            color: #888;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.2s;
        }
        .role-toggle button.active {
            background-color: #4ade80;
            color: #000;
        }
        .error-msg {
            color: #f87171;
            font-size: 12px;
            margin-top: 4px;
            display: none;
        }
        .trainer-fields {
            display: none;
        }
    </style>
</head>
<body>
<div class="card">
    <div class="logo">
        <div class="logo-icon">💪</div>
        <h1>FitNation</h1>
        <p>Modern fitness management</p>