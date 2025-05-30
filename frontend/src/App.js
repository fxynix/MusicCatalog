import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Link, useNavigate, useLocation } from 'react-router-dom';
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
    MailOutlined,
    LockOutlined
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
    const location = useLocation();

    const getSelectedMenuKey = () => {
        const path = location.pathname;
        if (path.startsWith('/tracks')) return '1';
        if (path.startsWith('/artists')) return '2';
        if (path.startsWith('/albums')) return '3';
        if (path.startsWith('/genres')) return '4';
        if (path.startsWith('/playlists')) return '5';
        if (path.startsWith('/users')) return '6';
        return '1';
    };

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
            message.error(error.response?.data || 'Login failed');
        }
    };

    const handleLogout = () => {
        setUser(null);
        localStorage.removeItem('user');
        message.success('Logged out successfully');

        if (location.pathname.includes('/users/') ||
            new URLSearchParams(location.search).has('authorId')) {
            navigate('/tracks');
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
                        <CustomerServiceOutlined style={{ color: '#fff', fontSize: '40px' }} />
                    </Link>
                </div>
                <Menu
                    theme="dark"
                    selectedKeys={[getSelectedMenuKey()]}
                    mode="inline"
                >
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
                                onClick={() => navigate(`/playlists?authorId=${user.id}&refresh=${Date.now()}`)}
                            >
                                My Playlists
                            </Button>
                            <UserDropdown user={user} onLogout={handleLogout} />
                        </>
                    ) : (
                        <Button
                            type="link"
                            icon={<UserOutlined style={{ fontSize: '24px' }}  />}
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
    const navigate = useNavigate();
    return (
        <Dropdown
            menu={{
                items: [
                    {
                        key: 'profile',
                        icon: <InfoCircleOutlined />,
                        label: 'Profile',
                        onClick: () => navigate(`/users/${user.id}/profile`)
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
        <span style={{ display: 'flex', alignItems: 'center', cursor: 'pointer', marginRight: 24 }}>
          <span style={{ marginRight: 8 }}>{user.name}</span>
          <UserOutlined style={{ fontSize: '24px' }} />
        </span>
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