import React, { useState, useEffect } from 'react';
import { Form, Input, Button, message } from 'antd';
import {UserOutlined, MailOutlined, LockOutlined, EditOutlined} from '@ant-design/icons';
import axios from "axios";

const UserProfilePage = ({ user, onUserUpdate }) => {
    const [form] = Form.useForm();
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (user) {
            form.setFieldsValue({
                name: user.name,
                email: user.email,
                password: ''
            });
        }
    }, [user, form]);

    const onFinish = async (values) => {
        try {
            setLoading(true);
            const response = await axios.patch(`${process.env.REACT_APP_API_URL}/users/${user.id}`,
                {
                    name: values.name,
                    email: values.email,
                    password: values.password || undefined
                }
            );
            onUserUpdate(response.data);
            message.success('Profile updated successfully!');
        } catch (error) {
            message.error('Failed to update profile');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ maxWidth: 600, margin: '0 auto' }}>
            <h2>User Profile</h2>
            <Form
                form={form}
                onFinish={onFinish}
                layout="vertical"
            >
                <Form.Item
                    name="name"
                    label="Username"
                    rules={[{ required: true, message: 'Please input your username!' }]}
                >
                    <Input prefix={<UserOutlined />} />
                </Form.Item>

                <Form.Item
                    name="email"
                    label="Email"
                    rules={[
                        { type: 'email', message: 'Please input valid email!' },
                        { required: true, message: 'Please input your email!' }
                    ]}
                >
                    <Input prefix={<MailOutlined />} />
                </Form.Item>

                <Form.Item
                    name="password"
                    label="Password"
                    rules={[{ message: 'Please input your password!' }]}
                >
                    <Input.Password prefix={<LockOutlined />} placeholder="Leave empty to keep current" />
                </Form.Item>

                <Form.Item>
                    <Button type="primary" htmlType="submit" loading={loading} icon={<EditOutlined />}>
                        Update Profile
                    </Button>
                </Form.Item>
            </Form>
        </div>
    );
};

export default UserProfilePage;