const BASE_URL = 'http://localhost:8080';

async function request(path, method = 'GET', body = null) {
    const options = {
        method,
        headers: { 'Content-Type': 'application/json' }
    };
    if (body) options.body = JSON.stringify(body);
    
    const response = await fetch(`${BASE_URL}${path}`, options);
    if (!response.ok) {
        const text = await response.text();
        throw new Error(`HTTP ${response.status}: ${text}`);
    }
    if (response.status === 204) return null;
    return await response.json();
}

async function runFullTest() {
    console.log('🔥 Bắt đầu quy trình quét lỗi toàn diện (Full API Scan)...\n');

    let state = {};

    try {
        // --- 1. GRADE COMPONENTS ---
        console.log('>>> [Module 1] Grade Components');
        const gcs = await request('/api/grade-components/data');
        console.log(`✅ Lấy danh sách thành công (${gcs.length} bản ghi)`);
        state.sectionId = gcs[0].courseSectionId;
        
        const newGC = await request('/api/grade-components', 'POST', {
            courseSectionId: state.sectionId,
            componentCode: 'TEST_FULL',
            componentName: 'Test toàn diện',
            weightPercentage: 15
        });
        state.tempGCId = newGC; // API này trả về UUID trực tiếp
        console.log(`✅ Tạo mới thành phần điểm thành công (ID: ${state.tempGCId})`);

        await request(`/api/grade-components/${state.tempGCId}`, 'DELETE');
        console.log(`✅ Xóa thành phần điểm test thành công`);


        // --- 2. GRADE SCALES ---
        console.log('\n>>> [Module 2] Grade Scales');
        const newScale = await request('/api/v1/grade-scales', 'POST', {
            scaleCode: 'FULL_TEST',
            minScore: 0.0,
            maxScore: 1.0,
            letterGrade: 'F',
            gpaValue: 0.0
        });
        state.tempScaleId = newScale.id;
        console.log(`✅ Tạo mới thang điểm thành công (ID: ${state.tempScaleId})`);

        await request(`/api/v1/grade-scales/${state.tempScaleId}`, 'DELETE');
        console.log(`✅ Xóa thang điểm test thành công`);


        // --- 3. STUDENT GRADES ---
        console.log('\n>>> [Module 3] Student Grades (CRUD)');
        const sgs = await request('/api/v1/student-grades');
        console.log(`✅ Lấy danh sách điểm sinh viên thành công (${sgs.length} bản ghi)`);
        
        if (sgs.length > 0) {
            state.sampleSG = sgs[0];
            // Test Update (PUT)
            const updatedSG = await request(`/api/v1/student-grades/${state.sampleSG.id}`, 'PUT', {
                ...state.sampleSG,
                score: 10.0,
                notes: 'Updated by script'
            });
            console.log(`✅ Cập nhật điểm ID ${state.sampleSG.id} lên 10.0 thành công`);
        }


        // --- 4. GRADE STUDENT (Business Logic) ---
        console.log('\n>>> [Module 4] Grade Entry Business Logic');
        if (sgs.length > 0 && gcs.length > 0) {
            const entry = await request('/api/grade-students', 'POST', {
                studentId: sgs[0].studentId,
                gradeComponentId: gcs[0].id,
                score: 8.8,
                notes: 'Nhập điểm chuyên cần test'
            });
            console.log(`✅ Nghiệp vụ nhập điểm cho Student ${sgs[0].studentId} thành công`);
        }

        console.log('\n🏆 CHÚC MỪNG! TẤT CẢ CÁC API ĐỀU HOẠT ĐỘNG HOÀN HẢO.');

    } catch (err) {
        console.error('\n❌ PHÁT HIỆN LỖI TRONG QUÁ TRÌNH TEST:');
        console.error('Lý do:', err.message);
        console.log('\n💡 Gợi ý của Mentor: Hãy kiểm tra log của Spring Boot để xem chi tiết lỗi SQL hoặc Logic.');
    }
}

runFullTest();
