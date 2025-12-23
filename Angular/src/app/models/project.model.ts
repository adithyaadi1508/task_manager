export interface Project {
  id?: number;
  name: string;
  description: string;
  startDate: string;
  endDate?: string;
  status: 'PLANNING' | 'ACTIVE' | 'ON_HOLD' | 'COMPLETED' | 'CANCELLED';
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  budget?: number;
  progress?: number;
  owner?: {
    id: number;
    username: string;
    firstName: string;
    lastName: string;
  };
  team?: any[];
  tasks?: any[];
  createdAt?: string;
  updatedAt?: string;
}

export interface ProjectStats {
  totalTasks: number;
  completedTasks: number;
  inProgressTasks: number;
  overdueTasks: number;
  teamMembers: number;
}