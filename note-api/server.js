const express = require('express');
const cors = require('cors');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const User = require('./models/User');
const { sequelize } = require('./models/index');

const app = express();
const PORT = process.env.PORT || 8000;
const JWT_SECRET = 'your-secret-key-change-this-in-production';

// Middleware
app.use(cors());
app.use(express.json());

// Initialize database
const initializeDatabase = async () => {
    try {
        await sequelize.sync();
        console.log('Database synchronized successfully');
    } catch (error) {
        console.error('Error synchronizing database:', error);
    }
};

initializeDatabase();

// Register endpoint
app.post('/api/auth/register', async (req, res) => {
    try {
        const { email, password, firstName, lastName } = req.body;

        // Validation
        if (!email || !password || !firstName || !lastName) {
            return res.status(400).json({
                success: false,
                message: 'All fields are required'
            });
        }

        // Check if user already exists
        const existingUser = await User.findOne({ where: { email } });
        if (existingUser) {
            return res.status(400).json({
                success: false,
                message: 'User already exists with this email'
            });
        }

        // Hash password
        const hashedPassword = await bcrypt.hash(password, 10);

        // Create new user in database
        const newUser = await User.create({
            email,
            password: hashedPassword,
            firstName,
            lastName,
            fullName: `${firstName} ${lastName}`
        });

        // Return user data (without password)
        const userData = newUser.toJSON();
        delete userData.password;

        res.status(201).json({
            success: true,
            message: 'User registered successfully',
            data: userData
        });

    } catch (error) {
        console.error('Registration error:', error);
        res.status(500).json({
            success: false,
            message: 'Internal server error'
        });
    }
});

// Login endpoint
app.post('/api/auth/login', async (req, res) => {
    try {
        const { email, password } = req.body;

        // Validation
        if (!email || !password) {
            return res.status(400).json({
                success: false,
                message: 'Email and password are required'
            });
        }

        // Find user in database
        const user = await User.findOne({ where: { email } });
        if (!user) {
            return res.status(401).json({
                success: false,
                message: 'Invalid email or password'
            });
        }

        // Check password
        const isPasswordValid = await bcrypt.compare(password, user.password);
        if (!isPasswordValid) {
            return res.status(401).json({
                success: false,
                message: 'Invalid email or password'
            });
        }

        // Generate JWT token
        const token = jwt.sign(
            { userId: user.id, email: user.email },
            JWT_SECRET,
            { expiresIn: '24h' }
        );

        // Return user data and token (without password)
        const userData = user.toJSON();
        delete userData.password;

        res.json({
            success: true,
            message: 'Login successful',
            data: userData,
            token
        });

    } catch (error) {
        console.error('Login error:', error);
        res.status(500).json({
            success: false,
            message: 'Internal server error'
        });
    }
});

// Health check endpoint
app.get('/health', async (req, res) => {
    try {
        const userCount = await User.count();
        res.json({
            status: 'OK',
            message: 'Note API is running',
            timestamp: new Date().toISOString(),
            users: userCount
        });
    } catch (error) {
        res.status(500).json({
            status: 'ERROR',
            message: 'Database error',
            error: error.message
        });
    }
});

// Get all users (for testing - remove in production)
app.get('/api/users', async (req, res) => {
    try {
        const users = await User.findAll({
            attributes: { exclude: ['password'] }
        });

        res.json({
            success: true,
            data: users
        });
    } catch (error) {
        res.status(500).json({
            success: false,
            message: 'Error retrieving users',
            error: error.message
        });
    }
});

// Start server
app.listen(PORT, () => {
    console.log(`🚀 Server running on http://localhost:${PORT}`);
    console.log(`📱 Android emulator should use: http://10.0.2.2:${PORT}`);
    console.log(`🌐 Health check: http://localhost:${PORT}/health`);
});

// Export for deployment platforms
module.exports = app;
