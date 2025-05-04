import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, useNavigate } from 'react-router-dom';
import { Layout, Menu, Dropdown, Modal, Form, Input, Button, message } from 'antd';
import {
    CustomerServiceOutlined,
    TeamOutlined,
    UserOutlined,
    FolderOutlined,
    PlaySquareOutlined,
    TagsOutlined,
    BookOutlined,
    LogoutOutlined,
    InfoCircleOutlined,
    UnorderedListOutlined,
    MailOutlined, LockOutlined
} from '@ant-design/icons';
import axios from 'axios';
import TrackList from "./components/TrackList";
import ArtistList from "./components/ArtistList";
import AlbumList from "./components/AlbumList";
import GenreList from "./components/GenreList";
import PlaylistList from "./components/PlaylistList";
import UserList from "./components/UserList";
import UserProfilePage from "./components/UserProfilePage";

const { Header, Sider, Content } = Layout;

const AppContent = () => {
    const [user, setUser] = useState(() => {
        const savedUser = localStorage.getItem('user');
        return savedUser ? JSON.parse(savedUser) : null;
    });
    const [isAuthModalVisible, setIsAuthModalVisible] = useState(false);
    const [authForm] = Form.useForm();
    const navigate = useNavigate();

    const handleAuth = async () => {
        try {
            const values = await authForm.validateFields();
            const response = await axios.post(`${process.env.REACT_APP_API_URL}/auth/login`, values);

            const userData = {
                id: response.data.userId,
                name: response.data.username,
                email: response.data.email,
                token: response.data.token
            };

            setUser(userData);
            localStorage.setItem('user', JSON.stringify(userData));
            setIsAuthModalVisible(false);
            message.success('Logged in successfully');
        } catch (error) {
            message.error(error.response?.data?.message || 'Login failed');
        }
    };

    const handleLogout = () => {
        setUser(null);
        localStorage.removeItem('user');
        message.success('Logged out successfully');

        if (location.pathname.includes('/users/') ||
            new URLSearchParams(location.search).has('authorId')) {
                navigate('/tracks'); // Перенаправляем на страницу треков
        }
    };


    const handleUserUpdate = (updatedUser) => {
        setUser(updatedUser);
        localStorage.setItem('user', JSON.stringify(updatedUser));
    };

    return (
        <Layout style={{ minHeight: '100vh' }}>
            <Sider collapsible>
                <div className="logo" style={{ height: '64px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Link to="/tracks">
                        <CustomerServiceOutlined style={{ color: '#fff', fontSize: '24px' }} />
                    </Link>
                </div>
                <Menu theme="dark" defaultSelectedKeys={['1']} mode="inline">
                    <Menu.Item key="1" icon={<PlaySquareOutlined />}>
                        <Link to="/tracks">Tracks</Link>
                    </Menu.Item>
                    <Menu.Item key="2" icon={<TeamOutlined />}>
                        <Link to="/artists">Artists</Link>
                    </Menu.Item>
                    <Menu.Item key="3" icon={<FolderOutlined />}>
                        <Link to="/albums">Albums</Link>
                    </Menu.Item>
                    <Menu.Item key="4" icon={<TagsOutlined />}>
                        <Link to="/genres">Genres</Link>
                    </Menu.Item>
                    <Menu.Item key="5" icon={<BookOutlined />}>
                        <Link to="/playlists">Playlists</Link>
                    </Menu.Item>
                    <Menu.Item key="6" icon={<UserOutlined />}>
                        <Link to="/users">Users</Link>
                    </Menu.Item>
                </Menu>
            </Sider>
            <Layout className="site-layout">
                <Header className="site-layout-background" style={{ padding: 0, display: 'flex', justifyContent: 'flex-end', alignItems: 'center' }}>
                    {user ? (
                        <>
                            <Button
                                type="link"
                                style={{ marginRight: 16 }}
                                icon={<UnorderedListOutlined />}
                                onClick={() => window.location.href = `/playlists?authorId=${user.id}&refresh=${Date.now()}`}
                            >
                                My Playlists
                            </Button>
                            <UserDropdown user={user} onLogout={handleLogout} />
                        </>
                    ) : (
                        <Button
                            type="link"
                            icon={<UserOutlined />}
                            style={{ marginRight: 24 }}
                            onClick={() => setIsAuthModalVisible(true)}
                        >
                            Login
                        </Button>
                    )}
                </Header>
                <Content style={{ margin: '16px' }}>
                    <div className="site-layout-background" style={{ padding: 24, minHeight: 360 }}>
                        <Routes>
                            <Route path="/tracks" element={<TrackList />} />
                            <Route path="/artists" element={<ArtistList />} />
                            <Route path="/albums" element={<AlbumList />} />
                            <Route path="/genres" element={<GenreList />} />
                            <Route path="/playlists" element={<PlaylistList />} />
                            <Route
                                path="/users"
                                element={<UserList currentUser={user} onUserUpdate={handleUserUpdate} />}
                            />
                            <Route
                                path="/users/:id/profile"
                                element={<UserProfilePage user={user} onUserUpdate={handleUserUpdate} />}
                            />
                            <Route path="/" element={<TrackList />} />
                        </Routes>
                    </div>
                </Content>
            </Layout>

            <Modal
                title="Login"
                open={isAuthModalVisible}
                onOk={handleAuth}
                onCancel={() => setIsAuthModalVisible(false)}
            >
                <Form form={authForm} layout="vertical">
                    <Form.Item
                        name="email"
                        label="Email"
                        rules={[
                            { required: true, message: 'Please input your email!' },
                            { type: 'email', message: 'Please enter a valid email!' }
                        ]}
                    >
                        <Input prefix={<MailOutlined />} />
                    </Form.Item>
                    <Form.Item
                        name="password"
                        label="Password"
                        rules={[{ required: true, message: 'Please input your password!' }]}
                    >
                        <Input.Password prefix={<LockOutlined />} />
                    </Form.Item>
                </Form>
            </Modal>
        </Layout>
    );
};

const UserDropdown = ({ user, onLogout }) => {
    return (
        <Dropdown
            menu={{
                items: [
                    {
                        key: 'profile',
                        icon: <InfoCircleOutlined />,
                        label: 'Profile',
                        onClick: () => window.location.href = `/users/${user.id}/profile`
                    },
                    {
                        key: 'logout',
                        icon: <LogoutOutlined />,
                        label: 'Logout',
                        onClick: onLogout
                    }
                ]
            }}
            trigger={['click']}
        >
            <div style={{ display: 'flex', alignItems: 'center', cursor: 'pointer', marginRight: 24 }}>
                <span style={{ marginRight: 8 }}>{user.name}</span>
                <UserOutlined style={{ fontSize: '20px' }} />
            </div>
        </Dropdown>
    );
};

const App = () => {
    return (
        <Router>
            <AppContent />
        </Router>
    );
};

export default App;